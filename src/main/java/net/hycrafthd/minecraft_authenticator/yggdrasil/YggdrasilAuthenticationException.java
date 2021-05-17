package net.hycrafthd.minecraft_authenticator.yggdrasil;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;

public class YggdrasilAuthenticationException extends AuthenticationException {
	
	private static final long serialVersionUID = 1L;
	
	public YggdrasilAuthenticationException(String message) {
		super(message);
	}
	
	public YggdrasilAuthenticationException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
