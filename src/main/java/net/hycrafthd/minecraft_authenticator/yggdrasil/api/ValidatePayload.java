package net.hycrafthd.minecraft_authenticator.yggdrasil.api;

public class ValidatePayload {
	
	private final String accessToken;
	private final String clientToken;
	
	public ValidatePayload(String accessToken, String clientToken) {
		this.accessToken = accessToken;
		this.clientToken = clientToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
	@Override
	public String toString() {
		return "ValidatePayload [accessToken=" + accessToken + ", clientToken=" + clientToken + "]";
	}
	
}
