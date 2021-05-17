package net.hycrafthd.minecraft_authenticator.microsoft;

import net.hycrafthd.minecraft_authenticator.login.User;

public class MicrosoftLoginResponse {
	
	private final User user;
	private final String refreshToken;
	
	public MicrosoftLoginResponse(User user, String refreshToken) {
		this.user = user;
		this.refreshToken = refreshToken;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
}
