package net.hycrafthd.minecraft_authenticator.login;

import java.net.URL;
import java.util.Optional;

import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftLoginResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftLoginRoutine;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;
import net.hycrafthd.minecraft_authenticator.util.AuthenticationUtil;
import net.hycrafthd.minecraft_authenticator.yggdrasil.YggdrasilLoginResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.YggdrasilLoginRoutine;

/**
 * <p>
 * Main class to authenticate a user with minecraft services. Currently mojang and microsoft accounts are supported.
 * <br>
 * First some information about yggdrasil and microsoft accounts
 * </p>
 * <p>
 * For security reasons you should not store username or passwords. With the yggdrasil mojang accounts you are able to
 * just login with username and password. The data will never be stored and you should never store it too. You should
 * use the {@link AuthenticationFile} and store that file as it does not contain the password and can be used to refresh
 * the access token for minecraft.
 * </p>
 * <p>
 * Microsoft accounts use oAuth. That is why you need to use a webbrowser to login with your microsoft account. You will
 * only get an authorization code which will be used for authentication. After the first login the
 * {@link AuthenticationFile} contains a refresh token which will be used for refreshing the authentication.
 * </p>
 * <p>
 * Even though the {@link AuthenticationFile} does not contain the real login data it contains sensitive data which can
 * be used to authenticate to certain services including your minecraft account. This file should therefore be kept
 * <b>secret</b> and should not be shared with others.
 * </p>
 * <p>
 * Here is an example to on how to login into a mojang account: <br>
 * <br>
 * For the first login you need a clientToken. This is a random uuid and should stay the same for all further requests.
 * Further more you need the username (email in most cases) and the password
 * 
 * <pre>
 * try {
 * 	final Authenticator authenticator = Authenticator.ofYggdrasil(clientToken, username, password).shouldAuthenticate().run();
 * 	final AuthenticationFile file = authenticator.getResultFile();
 * 	final Optional<User> user = authenticator.getUser();
 * 	// write authentication file e.g. with file.write(path)
 * } catch (AuthenticationException ex) {
 * 	ex.printStackTrace();
 * }
 * </pre>
 * 
 * You get a {@link AuthenticationFile} and a {@link Optional} with a user if there was not error and you called
 * {@link Builder#shouldAuthenticate()} before. After that the {@link AuthenticationFile} should be stored somewhere for
 * reuse.
 * </p>
 * <p>
 * Here is an example to on how to login into a microsoft account: <br>
 * <br>
 * To log into a microsoft account you need the authorization code that you get after you log into your microsoft
 * account. First you need to open the {@link #microsoftLogin()} url in a webbrowser (or in an integrated webbrowser
 * like javafx) and let the user login. After that you will be redirected to a page where the authorization code is the
 * <big>code</big> url parameters. The url looks like this:
 * https://login.live.com/oauth20_desktop.srf?code=M.XYZTHISISMYCODE
 * 
 * <pre>
 * try {
 * 	final Authenticator authenticator = Authenticator.ofMicrosoft(authorizationCode).shouldAuthenticate().run();
 * 	final AuthenticationFile file = authenticator.getResultFile();
 * 	final Optional<User> user = authenticator.getUser();
 * 	// write authentication file e.g. with file.write(path)
 * } catch (AuthenticationException ex) {
 * 	ex.printStackTrace();
 * }
 * </pre>
 * 
 * You get a {@link AuthenticationFile} and a {@link Optional} with a user if there was not error and you called
 * {@link Builder#shouldAuthenticate()} before. After that the {@link AuthenticationFile} should be stored somewhere for
 * reuse.
 * </p>
 * <p>
 * To refresh a session you can use the saved {@link AuthenticationFile} and login like that:
 * 
 * <pre>
 * try {
 * 	final Authenticator authenticator = Authenticator.of(authFile).shouldAuthenticate().run();
 * 	final AuthenticationFile file = authenticator.getResultFile();
 * 	final Optional<User> user = authenticator.getUser();
 * 	// write authentication file e.g. with file.write(path)
 * } catch (AuthenticationException ex) {
 * 	ex.printStackTrace();
 * }
 * </pre>
 * 
 * After that save the returned {@link AuthenticationFile} again. The session should stay for a relative long time, but
 * will be destroyed by certain events e.g. other client token, logout of all sessions, etc. The error message will tell
 * you why the user cannot be authenticated. If a token is not valid anymore the user must relogin.
 * </p>
 */
public class Authenticator {
	
	public static Builder of(AuthenticationFile file) {
		return new Builder(() -> file);
	}
	
	public static Builder ofMicrosoft(String authorizationCode) {
		return new Builder(() -> AuthenticationUtil.createMicrosoftAuthenticationFile(authorizationCode));
	}
	
	public static Builder ofYggdrasil(String clientToken, String username, String password) {
		return new Builder(() -> AuthenticationUtil.createYggdrasilAuthenticationFile(clientToken, username, password));
	}
	
	public static URL microsoftLogin() {
		return MicrosoftService.oAuthLoginUrl();
	}
	
	public static final class Builder {
		
		private final AuthenticationFileSupplier fileSupplier;
		private boolean authenticate;
		
		protected Builder(AuthenticationFileSupplier fileSupplier) {
			this.fileSupplier = fileSupplier;
		}
		
		/**
		 * Call this if you want to get a {@link User} object and authenticate to minecraft services
		 * 
		 * @return This builder
		 */
		public Builder shouldAuthenticate() {
			authenticate = true;
			return this;
		}
		
		/**
		 * This runs the tasks that were selected. If {@link #shouldAuthenticate()} is not enabled it will only resolve the
		 * {@link AuthenticationFile}
		 * 
		 * @return The authenticator object with the results
		 * @throws AuthenticationException
		 */
		public Authenticator run() throws AuthenticationException {
			return new Authenticator(fileSupplier, authenticate);
		}
		
	}
	
	private final AuthenticationFile resultFile;
	private final Optional<User> user;
	
	protected Authenticator(AuthenticationFileSupplier fileSupplier, boolean authenticate) throws AuthenticationException {
		final AuthenticationFile file = fileSupplier.get();
		
		AuthenticationFile resultFile = file;
		Optional<User> user = Optional.empty();
		
		// Authenticate with microsoft or yggdrasil
		if (authenticate) {
			final LoginResponse<? extends AuthenticationException> loginResponse;
			
			if (file instanceof AuthenticationFile.Microsoft) {
				final AuthenticationFile.Microsoft microsoftFile = (AuthenticationFile.Microsoft) file;
				final MicrosoftLoginResponse response = MicrosoftLoginRoutine.loginWithRefreshToken(microsoftFile.getRefreshToken());
				
				if (response.hasRefreshToken()) {
					resultFile = new AuthenticationFile.Microsoft(response.getRefreshToken().get());
				}
				
				loginResponse = response;
			} else if (file instanceof AuthenticationFile.Yggdrasil) {
				final AuthenticationFile.Yggdrasil yggdrasilFile = (AuthenticationFile.Yggdrasil) file;
				final YggdrasilLoginResponse response = YggdrasilLoginRoutine.loginWithAccessToken(yggdrasilFile.getAccessToken(), yggdrasilFile.getClientToken());
				
				if (response.hasAccessAndClientToken()) {
					resultFile = new AuthenticationFile.Yggdrasil(response.getAccessToken().get(), response.getClientToken().get());
				}
				loginResponse = response;
			} else {
				throw new AuthenticationException(file + " is not a microsoft or a yggdrasil file");
			}
			
			if (loginResponse.hasException()) {
				throw loginResponse.getException().get();
			}
			if (!loginResponse.hasUser()) {
				throw new AuthenticationException("After login there should be a user");
			}
			user = loginResponse.getUser();
		}
		
		this.resultFile = resultFile;
		this.user = user;
	}
	
	public AuthenticationFile getResultFile() {
		return resultFile;
	}
	
	public Optional<User> getUser() {
		return user;
	}
	
	@FunctionalInterface
	protected interface AuthenticationFileSupplier {
		
		AuthenticationFile get() throws AuthenticationException;
	}
	
}
