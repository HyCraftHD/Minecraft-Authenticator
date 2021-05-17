package net.hycrafthd.minecraft_authenticator.yggdrasil;

import net.hycrafthd.minecraft_authenticator.login.User;

public class YggdrasilLoginResponse {
	
	private final User user;
	private final String accessToken;
	private final String clientToken;
	
	public YggdrasilLoginResponse(User user, String accessToken, String clientToken) {
		this.user = user;
		this.accessToken = accessToken;
		this.clientToken = clientToken;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
}
