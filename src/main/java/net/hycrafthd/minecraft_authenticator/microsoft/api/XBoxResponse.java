package net.hycrafthd.minecraft_authenticator.microsoft.api;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class XBoxResponse {
	
	@SerializedName("IssueInstant")
	private final String issueInstant;
	@SerializedName("NotAfter")
	private final String notAfter;
	@SerializedName("Token")
	private final String token;
	@SerializedName("DisplayClaims")
	private final DisplayClaims displayClaims;
	
	public XBoxResponse(String issueInstant, String notAfter, String token, DisplayClaims displayClaims) {
		this.issueInstant = issueInstant;
		this.notAfter = notAfter;
		this.token = token;
		this.displayClaims = displayClaims;
	}
	
	public String getIssueInstant() {
		return issueInstant;
	}
	
	public String getNotAfter() {
		return notAfter;
	}
	
	public String getToken() {
		return token;
	}
	
	public DisplayClaims getDisplayClaims() {
		return displayClaims;
	}
	
	@Override
	public String toString() {
		return "XBoxResponse [issueInstant=" + issueInstant + ", notAfter=" + notAfter + ", token=" + token + ", displayClaims=" + displayClaims + "]";
	}
	
	public static class DisplayClaims {
		
		private final List<Xui> xui;
		
		public DisplayClaims(List<Xui> xui) {
			this.xui = xui;
		}
		
		public List<Xui> getXui() {
			return xui;
		}
		
		@Override
		public String toString() {
			return "DisplayClaims [xui=" + xui + "]";
		}
		
		public static class Xui {
			
			private final String uhs;
			
			public Xui(String uhs) {
				this.uhs = uhs;
			}
			
			public String getUhs() {
				return uhs;
			}
			
			@Override
			public String toString() {
				return "Xui [uhs=" + uhs + "]";
			}
		}
	}
	
}
