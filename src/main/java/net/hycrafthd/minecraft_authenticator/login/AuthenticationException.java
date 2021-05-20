package net.hycrafthd.minecraft_authenticator.login;

/**
 * Exception that is thrown if an error occurred while authentication.
 */
public class AuthenticationException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public AuthenticationException(String message) {
		super(message);
	}
	
	public AuthenticationException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
}
