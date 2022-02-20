package net.hycrafthd.minecraft_authenticator.microsoft.api;

import com.google.gson.annotations.SerializedName;

public class XBLAuthenticatePayload {
	
	@SerializedName("Properties")
	private final Properties properties;
	@SerializedName("RelyingParty")
	private final String relyingParty;
	@SerializedName("TokenType")
	private final String tokenType;
	
	public XBLAuthenticatePayload(Properties properties, String relyingParty, String tokenType) {
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
		return "XBLAuthenticatePayload [properties=" + properties + ", relyingParty=" + relyingParty + ", tokenType=" + tokenType + "]";
	}
	
	public static class Properties {
		
		@SerializedName("AuthMethod")
		private final String authMethod;
		@SerializedName("SiteName")
		private final String siteName;
		@SerializedName("RpsTicket")
		private final String rpsTicket;
		
		public Properties(String authMethod, String siteName, String rpsTicket) {
			this.authMethod = authMethod;
			this.siteName = siteName;
			this.rpsTicket = rpsTicket;
		}
		
		public String getAuthMethod() {
			return authMethod;
		}
		
		public String getSiteName() {
			return siteName;
		}
		
		public String getRpsTicket() {
			return rpsTicket;
		}
		
		@Override
		public String toString() {
			return "Properties [authMethod=" + authMethod + ", siteName=" + siteName + ", rpsTicket=" + rpsTicket + "]";
		}
	}
}
