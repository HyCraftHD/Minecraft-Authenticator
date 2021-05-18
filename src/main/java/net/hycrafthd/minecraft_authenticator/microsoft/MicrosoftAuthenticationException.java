package net.hycrafthd.minecraft_authenticator.microsoft;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;

public class MicrosoftAuthenticationException extends AuthenticationException {
	
	private static final long serialVersionUID = 1L;
	
	public MicrosoftAuthenticationException(String message) {
		super(message);
	}
	
	public MicrosoftAuthenticationException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
