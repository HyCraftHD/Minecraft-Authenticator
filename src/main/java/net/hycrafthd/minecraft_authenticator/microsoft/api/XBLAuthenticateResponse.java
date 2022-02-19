package net.hycrafthd.minecraft_authenticator.microsoft.api;

public class XBLAuthenticateResponse extends XBoxResponse {
	
	public XBLAuthenticateResponse(String issueInstant, String notAfter, String token, DisplayClaims displayClaims) {
		super(issueInstant, notAfter, token, displayClaims);
	}
	
}