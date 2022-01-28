package net.hycrafthd.minecraft_authenticator.login;

import java.util.Optional;

/**
 * Internal use only
 */
public interface LoginResponse<E extends AuthenticationException> {
	
	public boolean hasUser();
	
	public Optional<User> getUser();
	
	public boolean hasException();
	
	public Optional<E> getException();
	
}
