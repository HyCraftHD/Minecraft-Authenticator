package net.hycrafthd.minecraft_authenticator.login;

import java.net.URL;
import java.net.URLConnection;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.Optional;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.login.file.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.login.file.MicrosoftAuthenticationFile;
import net.hycrafthd.minecraft_authenticator.login.file.YggdrasilAuthenticationFile;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftLoginResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftLoginRoutine;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;
import net.hycrafthd.minecraft_authenticator.util.AuthenticationUtil;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil.TimeoutValues;

/**
 * <p>
 * Main class to authenticate a user with minecraft services. Currently microsoft accounts are supported. <br>
 * First some information about yggdrasil and microsoft accounts:
 * </p>
 * <p>
 * For security reasons you should not store username or passwords. With the yggdrasil mojang accounts you are able to
 * just login with username and password. The data will never be stored and you should never store it too. You should
 * use the {@link AuthenticationFile} and store that file as it does not contain the password and can be used to refresh
 * the access token for minecraft.
 * </p>
 * <p>
 * Microsoft accounts use oAuth. That is why you need to use a browser to login with your microsoft account. You will
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
 * </p>
 * 
 * <pre>
 * try {
 * 	final Authenticator authenticator = Authenticator.ofYggdrasil(clientToken, username, password).shouldAuthenticate().run();
 * 	final AuthenticationFile file = authenticator.getResultFile();
 * 	final Optional user = authenticator.getUser();
 * 	// write authentication file e.g. with file.write(os)
 * } catch (AuthenticationException ex) {
 * 	ex.printStackTrace();
 * }
 * </pre>
 * <p>
 * You get an {@link AuthenticationFile} and an {@link Optional} with a user if there was no error and you called
 * {@link Builder#shouldAuthenticate()} before. After that the {@link AuthenticationFile} should be stored somewhere for
 * reuse.
 * </p>
 * <p>
 * Here is an example to on how to login into a microsoft account: <br>
 * <br>
 * This description only covers the basic oAuth for microsoft with the minecraft launcher id. If you want to customize
 * the login you should create a custom azure application and use the custom azure authentication method
 * {@link Builder#customAzureApplication(String, String)}. <br>
 * To log into a microsoft account you need the authorization code that you get after you log into your microsoft
 * account. First you need to open the {@link #microsoftLogin()} url in a browser (or in an integrated browser like
 * javafx) and let the user login. After that you will be redirected to a page where the authorization code is the code
 * url parameter. The url looks like this: https://login.live.com/oauth20_desktop.srf?code=M.XYZTHISISMYCODE
 * </p>
 * 
 * <pre>
 * try {
 * 	final Authenticator authenticator = Authenticator.ofMicrosoft(authorizationCode).shouldAuthenticate().run();
 * 	final AuthenticationFile file = authenticator.getResultFile();
 * 	final Optional user = authenticator.getUser();
 * 	// write authentication file e.g. with file.write(os)
 * } catch (AuthenticationException ex) {
 * 	ex.printStackTrace();
 * }
 * </pre>
 * <p>
 * You get an {@link AuthenticationFile} and an {@link Optional} with a user if there was no error and you called
 * {@link Builder#shouldAuthenticate()} before. After that the {@link AuthenticationFile} should be stored somewhere for
 * reuse.
 * </p>
 * <p>
 * To refresh a session you can use the saved {@link AuthenticationFile} and login like that:
 * </p>
 * 
 * <pre>
 * try {
 * 	final Authenticator authenticator = Authenticator.of(authFile).shouldAuthenticate().run();
 * 	final AuthenticationFile file = authenticator.getResultFile();
 * 	final Optional user = authenticator.getUser();
 * 	// write authentication file e.g. with file.write(os)
 * } catch (AuthenticationException ex) {
 * 	ex.printStackTrace();
 * }
 * </pre>
 * <p>
 * After that save the returned {@link AuthenticationFile} again. The session should stay for a relative long time, but
 * will be destroyed by certain events e.g. other client token, logout of all sessions, etc. The error message will tell
 * you why the user cannot be authenticated. If a token is not valid anymore the user must relogin.
 * </p>
 */
public class Authenticator {
	
	/**
	 * Creates an {@link Authenticator} of a {@link AuthenticationFile}.
	 * 
	 * @see Authenticator
	 * @param file The {@link AuthenticationFile}
	 * @return A {@link Builder} to configure the authenticator
	 */
	public static Builder of(AuthenticationFile file) {
		return new Builder(timeoutValues -> file);
	}
	
	/**
	 * Creates a microsoft {@link Authenticator} with a microsoft authorization code. See examples in the class javadoc.
	 * 
	 * @see Authenticator
	 * @param authorizationCode Microsoft authorization code of the redirect url
	 * @return A {@link Builder} to configure the authenticator
	 */
	public static Builder ofMicrosoft(String authorizationCode) {
		return new Builder((customAzureApplication, timeoutValues) -> AuthenticationUtil.createMicrosoftAuthenticationFile(customAzureApplication, authorizationCode, timeoutValues));
	}
	
	/**
	 * Returns the minecraft launcher oAuth login url for microsoft accounts. After the browser login the authorization code
	 * can be extracted from the redirect url.
	 * 
	 * @see Authenticator
	 * @see Authenticator#microsoftLoginRedirect()
	 * @return oAuth microsoft login url
	 */
	public static URL microsoftLogin() {
		return MicrosoftService.oAuthLoginUrl();
	}
	
	/**
	 * Return the minecraft launcher oAuth redirect url. This returns the start of the redirect url and should be used to
	 * match the redirect url and then to extract the authorization code.
	 * 
	 * @see Authenticator
	 * @return oAuth microsoft redirect url
	 */
	public static String microsoftLoginRedirect() {
		return Constants.MICROSOFT_OAUTH_REDIRECT_URL;
	}
	
	/**
	 * Returns the oAuth login url for your custom azure application. You need to extract the authorization code of the
	 * redirect url (depends on how you setup your azure application). If you don't want to use a custom azure application
	 * look at {@link Authenticator#microsoftLogin()}.
	 * 
	 * @see Authenticator
	 * @param clientId The azure client id
	 * @param redirectUrl The configured redirect url
	 * @return oAuth microsoft login url
	 */
	public static URL microsoftLogin(String clientId, String redirectUrl) {
		return MicrosoftService.oAuthLoginUrl(clientId, redirectUrl);
	}
	
	/**
	 * Internal builder class
	 */
	public static class Builder {
		
		private final AuthenticationFileFunctionWithCustomAzureApplication fileFunction;
		private boolean authenticate;
		private Optional<Entry<String, String>> customAzureApplication;
		private int serviceConnectTimeout;
		private int serviceReadTimeout;
		
		/**
		 * Accepts a {@link AuthenticationFileFunction} which is just a normal supplier for an {@link AuthenticationFile} which
		 * can throw an {@link AuthenticationException}
		 * 
		 * @param fileSupplier Supplier that returns {@link AuthenticationFile} for authentication
		 */
		protected Builder(AuthenticationFileFunction fileSupplier) {
			this((customAzureApplication, timeoutValues) -> fileSupplier.get(timeoutValues));
		}
		
		/**
		 * Accepts a {@link AuthenticationFileFunctionWithCustomAzureApplication} which supplies the custom azure application
		 * values and returns a {@link AuthenticationFile} which can throw an {@link AuthenticationException}
		 * 
		 * @param fileFunction Function that returns {@link AuthenticationFile} for authentication
		 */
		protected Builder(AuthenticationFileFunctionWithCustomAzureApplication fileFunction) {
			this.fileFunction = fileFunction;
			authenticate = false;
			customAzureApplication = Optional.empty();
			serviceConnectTimeout = 15000;
			serviceReadTimeout = 15000;
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
		 * Call this if you have a custom azure application that will handle the oauth for microsoft accounts
		 * 
		 * @param clientId The azure client id
		 * @param redirectUrl The redirect url
		 * @return This builder
		 */
		public Builder customAzureApplication(String clientId, String redirectUrl) {
			customAzureApplication = Optional.of(new SimpleImmutableEntry<>(clientId, redirectUrl));
			return this;
		}
		
		/**
		 * Configure the connect timeout of a service request. This timeout configures
		 * {@link URLConnection#setConnectTimeout(int)} to the passed value for each service request
		 * 
		 * @param timeout Timeout in milliseconds
		 * @return This builder
		 */
		public Builder serviceConnectTimeout(int timeout) {
			serviceConnectTimeout = timeout;
			return this;
		}
		
		/**
		 * Configure the read timeout of a service request. This timeout configures {@link URLConnection#setReadTimeout(int)} to
		 * the passed value for each service request
		 * 
		 * @param timeout Timeout in milliseconds
		 * @return This builder
		 */
		public Builder serviceReadTimeout(int timeout) {
			serviceReadTimeout = timeout;
			return this;
		}
		
		/**
		 * This runs the tasks that were selected. If {@link #shouldAuthenticate()} is not enabled it will only resolve the
		 * {@link AuthenticationFile}. This call is blocking and can take some time if the services take a long respond time.
		 * The default timeout time is 15 seconds per service request. Change the timeout for the services with
		 * {@link #serviceConnectTimeout(int)} and {@link #serviceReadTimeout(int)}
		 * 
		 * @return The authenticator object with the results
		 * @throws AuthenticationException Throws exception if login was not successful
		 */
		public Authenticator run() throws AuthenticationException {
			return new Authenticator(fileFunction, authenticate, customAzureApplication, new TimeoutValues(serviceConnectTimeout, serviceReadTimeout));
		}
		
	}
	
	private final AuthenticationFile resultFile;
	private final Optional<User> user;
	
	/**
	 * Internal constructor that runs the authentication
	 * 
	 * @param fileFunction Function that returns {@link AuthenticationFile} for authentication
	 * @param authenticate Should authenticate to get a {@link User} as a result
	 * @param customAzureApplication Optional value to pass custom azure application values
	 * @param timeoutValues Timeout values for a service connection
	 * @throws AuthenticationException Throws exception if authentication was not successful
	 */
	protected Authenticator(AuthenticationFileFunctionWithCustomAzureApplication fileFunction, boolean authenticate, Optional<Entry<String, String>> customAzureApplication, TimeoutValues timeoutValues) throws AuthenticationException {
		final AuthenticationFile file = fileFunction.get(customAzureApplication, timeoutValues);
		
		AuthenticationFile resultFile = file;
		Optional<User> user = Optional.empty();
		
		// Authenticate with microsoft or yggdrasil
		if (authenticate) {
			final LoginResponse<? extends AuthenticationException> loginResponse;
			
			if (file instanceof MicrosoftAuthenticationFile) {
				// Microsoft authentication
				final MicrosoftAuthenticationFile microsoftFile = (MicrosoftAuthenticationFile) file;
				
				final MicrosoftLoginResponse response;
				if (customAzureApplication.isPresent()) {
					final Entry<String, String> entry = customAzureApplication.get();
					final String clientId = entry.getKey();
					final String redirectUrl = entry.getValue();
					response = MicrosoftLoginRoutine.loginWithRefreshToken(clientId, redirectUrl, microsoftFile.getRefreshToken(), timeoutValues);
				} else {
					response = MicrosoftLoginRoutine.loginWithRefreshToken(microsoftFile.getRefreshToken(), timeoutValues);
				}
				
				if (response.hasRefreshToken()) {
					resultFile = new MicrosoftAuthenticationFile(response.getRefreshToken().get());
				}
				
				loginResponse = response;
			} else if (file instanceof YggdrasilAuthenticationFile) {
				throw new AuthenticationException("Yggdrasil is outdated and does not work anymore");
			} else {
				throw new AuthenticationException(file + " is not a microsoft or a yggdrasil file");
			}
			
			// Validate authentication response
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
	
	/**
	 * Returns the updated {@link AuthenticationFile} if authentication was requested. Else returns the initial object.
	 * 
	 * @return {@link AuthenticationFile} that should be used for the next authentication.
	 */
	public AuthenticationFile getResultFile() {
		return resultFile;
	}
	
	/**
	 * Returns the user if authentication was requested and no error occured.
	 * 
	 * @return Should not be empty if authentication was requested and not {@link AuthenticationException} was raised.
	 */
	public Optional<User> getUser() {
		return user;
	}
	
	/**
	 * Supplier that returns an {@link AuthenticationFile} and can trow an {@link AuthenticationException}
	 */
	@FunctionalInterface
	protected interface AuthenticationFileFunction {
		
		/**
		 * Returns the {@link AuthenticationFile}
		 * 
		 * @param timeoutValues Timeout values for a service connection
		 * @return {@link AuthenticationFile}
		 * @throws AuthenticationException Throws if authentication file is created with an online service with authentication
		 */
		AuthenticationFile get(TimeoutValues timeoutValues) throws AuthenticationException;
	}
	
	/**
	 * Function that returns an {@link AuthenticationFile} and can trow an {@link AuthenticationException} and supplied the
	 * custom azure application parameters
	 */
	@FunctionalInterface
	protected interface AuthenticationFileFunctionWithCustomAzureApplication {
		
		/**
		 * Returns the {@link AuthenticationFile}
		 * 
		 * @param customAzureApplication Custom azure application values that is needed to handle the oauth for microsoft
		 *        accounts
		 * @param timeoutValues Timeout values for a service connection
		 * @return {@link AuthenticationFile}
		 * @throws AuthenticationException Throws if authentication file is created with an online service with authentication
		 */
		AuthenticationFile get(Optional<Entry<String, String>> customAzureApplication, TimeoutValues timeoutValues) throws AuthenticationException;
	}
	
}
