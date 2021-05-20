package net.hycrafthd.minecraft_authenticator.login;

import java.util.Optional;

import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftLoginResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftLoginRoutine;
import net.hycrafthd.minecraft_authenticator.util.AuthenticationUtil;
import net.hycrafthd.minecraft_authenticator.yggdrasil.YggdrasilLoginResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.YggdrasilLoginRoutine;

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
	
	public static final class Builder {
		
		private final AuthenticationFileSupplier fileSupplier;
		private boolean authenticate;
		
		private Builder(AuthenticationFileSupplier fileSupplier) {
			this.fileSupplier = fileSupplier;
		}
		
		public Builder shouldAuthenticate() {
			authenticate = true;
			return this;
		}
		
		public Authenticator run() throws AuthenticationException {
			return new Authenticator(fileSupplier, authenticate);
		}
		
	}
	
	private final AuthenticationFile resultFile;
	private final Optional<User> user;
	
	public Authenticator(AuthenticationFileSupplier fileSupplier, boolean authenticate) throws AuthenticationException {
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
	private interface AuthenticationFileSupplier {
		
		AuthenticationFile get() throws AuthenticationException;
	}
	
}
