package net.hycrafthd.minecraft_authenticator.yggdrasil.api;

public class RefreshPayload {
	
	private final String accessToken;
	private final String clientToken;
	private final boolean requestUser;
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public RefreshPayload(String accessToken, String clientToken, boolean requestUser) {
		this.accessToken = accessToken;
		this.clientToken = clientToken;
		this.requestUser = requestUser;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
	public boolean isRequestUser() {
		return requestUser;
	}
	
	@Override
	public String toString() {
		return "RefreshPayload [accessToken=" + accessToken + ", clientToken=" + clientToken + ", requestUser=" + requestUser + "]";
	}
	
}
