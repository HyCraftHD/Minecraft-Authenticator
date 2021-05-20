package net.hycrafthd.minecraft_authenticator.yggdrasil;

import java.util.Optional;

import net.hycrafthd.minecraft_authenticator.login.LoginResponse;
import net.hycrafthd.minecraft_authenticator.login.User;

public class YggdrasilLoginResponse implements LoginResponse<YggdrasilAuthenticationException> {
	
	public static YggdrasilLoginResponse ofSuccess(User user, String accessToken, String clientToken) {
		return new YggdrasilLoginResponse(Optional.of(user), Optional.of(accessToken), Optional.of(clientToken), Optional.empty());
	}
	
	public static YggdrasilLoginResponse ofError(YggdrasilAuthenticationException exception) {
		return new YggdrasilLoginResponse(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(exception));
	}
	
	private final Optional<User> user;
	private final Optional<String> accessToken;
	private final Optional<String> clientToken;
	private final Optional<YggdrasilAuthenticationException> exception;
	
	private YggdrasilLoginResponse(Optional<User> user, Optional<String> accessToken, Optional<String> clientToken, Optional<YggdrasilAuthenticationException> exception) {
		this.user = user;
		this.accessToken = accessToken;
		this.clientToken = clientToken;
		this.exception = exception;
	}
	
	@Override
	public boolean hasUser() {
		return user.isPresent();
	}
	
	@Override
	public Optional<User> getUser() {
		return user;
	}
	
	public boolean hasAccessAndClientToken() {
		return accessToken.isPresent() && clientToken.isPresent();
	}
	
	public Optional<String> getAccessToken() {
		return accessToken;
	}
	
	public Optional<String> getClientToken() {
		return clientToken;
	}
	
	@Override
	public boolean hasException() {
		return exception.isPresent();
	}
	
	@Override
	public Optional<YggdrasilAuthenticationException> getException() {
		return exception;
	}
}
