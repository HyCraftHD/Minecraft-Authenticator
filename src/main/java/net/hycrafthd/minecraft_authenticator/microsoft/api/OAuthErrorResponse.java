package net.hycrafthd.minecraft_authenticator.microsoft.api;

import com.google.gson.annotations.SerializedName;

public class OAuthErrorResponse {
	
	private final String error;
	@SerializedName("error_description")
	private final String errorDescription;
	@SerializedName("correlation_id")
	private final String correlationId;
	
	public OAuthErrorResponse(String error, String errorDescription, String correlationId) {
		this.error = error;
		this.errorDescription = errorDescription;
		this.correlationId = correlationId;
	}
	
	public String getError() {
		return error;
	}
	
	public String getErrorDescription() {
		return errorDescription;
	}
	
	public String getCorrelationId() {
		return correlationId;
	}
	
	@Override
	public String toString() {
		return "OAuthErrorResponse [error=" + error + ", errorDescription=" + errorDescription + ", correlationId=" + correlationId + "]";
	}
	
}