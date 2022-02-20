package net.hycrafthd.minecraft_authenticator.microsoft.api;

import com.google.gson.annotations.SerializedName;

public class XSTSAuthorizeErrorResponse {
	
	@SerializedName("Identity")
	private final String identity;
	@SerializedName("XErr")
	private final long xErr;
	@SerializedName("Message")
	private final String message;
	@SerializedName("Redirect")
	private final String redirect;
	
	public XSTSAuthorizeErrorResponse(String identity, long xErr, String message, String redirect) {
		this.identity = identity;
		this.xErr = xErr;
		this.message = message;
		this.redirect = redirect;
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public long getxErr() {
		return xErr;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getRedirect() {
		return redirect;
	}
	
	@Override
	public String toString() {
		return "XSTSAuthorizeErrorResponse [identity=" + identity + ", xErr=" + xErr + ", message=" + message + ", redirect=" + redirect + "]";
	}
	
}
