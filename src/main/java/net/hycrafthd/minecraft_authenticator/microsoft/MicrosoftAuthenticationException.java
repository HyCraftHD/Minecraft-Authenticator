package net.hycrafthd.minecraft_authenticator.microsoft;

import java.util.Optional;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;

public class MicrosoftAuthenticationException extends AuthenticationException {
	
	private static final long serialVersionUID = 1L;
	
	private final Optional<String> refreshToken;
	
	public MicrosoftAuthenticationException(String message, String refreshToken) {
		super(message);
		this.refreshToken = Optional.ofNullable(refreshToken);
	}
	
	public MicrosoftAuthenticationException(String message, Throwable throwable, String refreshToken) {
		super(message, throwable);
		this.refreshToken = Optional.ofNullable(refreshToken);
	}
	
	public Optional<String> getRefreshToken() {
		return refreshToken;
	}
	
}
