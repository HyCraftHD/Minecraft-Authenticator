package net.hycrafthd.minecraft_authenticator.login;

import java.io.IOException;
import java.nio.file.Path;
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
	
	public static Builder of(Path path) {
		return new Builder(() -> AuthenticationUtil.readAuthenticationFile(path));
	}
	
	public static Builder of(String authorizationCode) {
		return new Builder(() -> AuthenticationUtil.createMicrosoftAuthenticationFile(authorizationCode));
	}
	
	public static Builder of(String clientToken, String username, String password) {
		return new Builder(() -> AuthenticationUtil.createYggdrasilAuthenticationFile(clientToken, username, password));
	}
	
	public static final class Builder {
		
		private final AuthenticationFileSupplier file;
		private Path createAuthFile;
		private Path updateAuthFile;
		private boolean authenticate;
		
		private Builder(AuthenticationFileSupplier file) {
			this.file = file;
		}
		
		public Builder shouldCreateAuthFile(Path path) {
			createAuthFile = path;
			return this;
		}
		
		public Builder shouldUpdateAuthFile(Path path) {
			updateAuthFile = path;
			return this;
		}
		
		public Builder shouldAuthenticate() {
			authenticate = true;
			return this;
		}
		
		public Authenticator build() {
			return new Authenticator(file, createAuthFile, updateAuthFile, authenticate);
		}
		
	}
	
	private final AuthenticationFileSupplier file;
	private final Path createAuthFile;
	private final Path updateAuthFile;
	private final boolean authenticate;
	
	public Authenticator(AuthenticationFileSupplier file, Path createAuthFile, Path updateAuthFile, boolean authenticate) {
		this.file = file;
		this.createAuthFile = createAuthFile;
		this.updateAuthFile = updateAuthFile;
		this.authenticate = authenticate;
	}
	
	public Optional<User> run() throws IOException, AuthenticationException {
		if (createAuthFile != null) {
			AuthenticationUtil.writeAuthenticationFile(file.get(), createAuthFile);
		}
		if (authenticate) {
			if (file instanceof AuthenticationFile.Microsoft) {
				return runMicrosoftAuthentication((AuthenticationFile.Microsoft) file);
			} else if (file instanceof AuthenticationFile.Yggdrasil) {
				return runYggdrasilAuthentication((AuthenticationFile.Yggdrasil) file);
			}
		}
		return Optional.empty();
	}
	
	private Optional<User> runMicrosoftAuthentication(final AuthenticationFile.Microsoft microsoftFile) throws IOException, AuthenticationException {
		final MicrosoftLoginResponse response = MicrosoftLoginRoutine.loginWithRefreshToken(microsoftFile.getRefreshToken());
		
		if (updateAuthFile != null && response.hasRefreshToken()) {
			AuthenticationUtil.writeAuthenticationFile(new AuthenticationFile.Microsoft(response.getRefreshToken().get()), createAuthFile);
		}
		if (response.hasException()) {
			throw response.getException().get();
		}
		if (!response.hasUser()) {
			throw new AuthenticationException("After login there should be a user");
		}
		return response.getUser();
	}
	
	private Optional<User> runYggdrasilAuthentication(final AuthenticationFile.Yggdrasil yggdrasilFile) throws IOException, AuthenticationException {
		final YggdrasilLoginResponse response = YggdrasilLoginRoutine.loginWithAccessToken(yggdrasilFile.getAccessToken(), yggdrasilFile.getClientToken());
		
		if (updateAuthFile != null && response.hasAccessAndClientToken()) {
			AuthenticationUtil.writeAuthenticationFile(new AuthenticationFile.Yggdrasil(response.getAccessToken().get(), response.getClientToken().get()), createAuthFile);
		}
		if (response.hasException()) {
			throw response.getException().get();
		}
		if (!response.hasUser()) {
			throw new AuthenticationException("After login there should be a user");
		}
		return response.getUser();
	}
	
	@FunctionalInterface
	private interface AuthenticationFileSupplier {
		
		AuthenticationFile get() throws IOException, AuthenticationException;
	}
	
}
