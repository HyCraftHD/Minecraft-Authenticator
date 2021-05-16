package net.hycrafthd.minecraft_authenticator.microsoft.api;

import com.google.gson.annotations.SerializedName;

public class XSTSAuthorizeErrorResponse {
	
	@SerializedName("Identity")
	private String identity;
	@SerializedName("XErr")
	private long xErr;
	@SerializedName("Message")
	private String message;
	@SerializedName("Redirect")
	private String redirect;
	
	public XSTSAuthorizeErrorResponse(String identity, long xErr, String message, String redirect) {
		this.identity = identity;
		this.xErr = xErr;
		this.message = message;
		this.redirect = redirect;
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	public long getxErr() {
		return xErr;
	}
	
	public void setxErr(long xErr) {
		this.xErr = xErr;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getRedirect() {
		return redirect;
	}
	
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
	
	@Override
	public String toString() {
		return "XSTSAuthorizeErrorResponse [identity=" + identity + ", xErr=" + xErr + ", message=" + message + ", redirect=" + redirect + "]";
	}
	
}
