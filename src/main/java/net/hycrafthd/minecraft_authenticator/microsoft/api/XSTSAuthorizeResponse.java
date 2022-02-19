package net.hycrafthd.minecraft_authenticator.microsoft.api;

public class XSTSAuthorizeResponse extends XBoxResponse {
	
	public XSTSAuthorizeResponse(String issueInstant, String notAfter, String token, DisplayClaims displayClaims) {
		super(issueInstant, notAfter, token, displayClaims);
	}
	
}