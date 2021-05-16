package net.hycrafthd.minecraft_authenticator.microsoft.api;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class XSTSAuthorizePayload {
	
	@SerializedName("Properties")
	private final Properties properties;
	@SerializedName("RelyingParty")
	private final String relyingParty;
	@SerializedName("TokenType")
	private final String tokenType;
	
	public XSTSAuthorizePayload(Properties properties, String relyingParty, String tokenType) {
		this.properties = properties;
		this.relyingParty = relyingParty;
		this.tokenType = tokenType;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public String getRelyingParty() {
		return relyingParty;
	}
	
	public String getTokenType() {
		return tokenType;
	}
	
	@Override
	public String toString() {
		return "XSTSAuthorizePayload [properties=" + properties + ", relyingParty=" + relyingParty + ", tokenType=" + tokenType + "]";
	}
	
	public static class Properties {
		
		@SerializedName("SandboxId")
		private final String sandboxId;
		@SerializedName("UserTokens")
		private final List<String> userTokens;
		
		public Properties(String sandboxId, List<String> userTokens) {
			this.sandboxId = sandboxId;
			this.userTokens = userTokens;
		}
		
		public String getSandboxId() {
			return sandboxId;
		}
		
		public List<String> getUserTokens() {
			return userTokens;
		}
		
		@Override
		public String toString() {
			return "Properties [sandboxId=" + sandboxId + ", userTokens=" + userTokens + "]";
		}
	}
}
