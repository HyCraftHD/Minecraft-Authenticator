package net.hycrafthd.minecraft_authenticator.microsoft.api;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MinecraftLauncherLoginResponse {
	
	private final String username;
	private final List<String> roles;
	@SerializedName("access_token")
	private final String accessToken;
	@SerializedName("token_type")
	private final String tokenType;
	@SerializedName("expires_in")
	private final long expiresIn;
	
	public MinecraftLauncherLoginResponse(String username, List<String> roles, String accessToken, String tokenType, long expiresIn) {
		this.username = username;
		this.roles = roles;
		this.accessToken = accessToken;
		this.tokenType = tokenType;
		this.expiresIn = expiresIn;
	}
	
	public String getUsername() {
		return username;
	}
	
	public List<String> getRoles() {
		return roles;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getTokenType() {
		return tokenType;
	}
	
	public long getExpiresIn() {
		return expiresIn;
	}
	
	@Override
	public String toString() {
		return "MinecraftLauncherLoginResponse [username=" + username + ", roles=" + roles + ", accessToken=" + accessToken + ", tokenType=" + tokenType + ", expiresIn=" + expiresIn + "]";
	}
	
}
