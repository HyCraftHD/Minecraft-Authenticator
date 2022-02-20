package net.hycrafthd.minecraft_authenticator.microsoft.api;

import com.google.gson.annotations.SerializedName;

public class OAuthTokenResponse {
	
	@SerializedName("token_type")
	private final String tokenType;
	@SerializedName("expires_in")
	private final long expiredIn;
	private final String scope;
	@SerializedName("access_token")
	private final String accessToken;
	@SerializedName("refresh_token")
	private final String refreshToken;
	@SerializedName("user_id")
	private final String userId;
	private final int foci;
	
	public OAuthTokenResponse(String tokenType, long expiredIn, String scope, String accessToken, String refreshToken, String userId, int foci) {
		this.tokenType = tokenType;
		this.expiredIn = expiredIn;
		this.scope = scope;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.userId = userId;
		this.foci = foci;
	}
	
	public String getTokenType() {
		return tokenType;
	}
	
	public long getExpiredIn() {
		return expiredIn;
	}
	
	public String getScope() {
		return scope;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public int getFoci() {
		return foci;
	}
	
	@Override
	public String toString() {
		return "OAuthTokenResponse [tokenType=" + tokenType + ", expiredIn=" + expiredIn + ", scope=" + scope + ", accessToken=" + accessToken + ", refreshToken=" + refreshToken + ", userId=" + userId + ", foci=" + foci + "]";
	}
	
}