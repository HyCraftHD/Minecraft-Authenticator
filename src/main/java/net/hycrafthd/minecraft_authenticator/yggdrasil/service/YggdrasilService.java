package net.hycrafthd.minecraft_authenticator.yggdrasil.service;

import java.io.IOException;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil;
import net.hycrafthd.minecraft_authenticator.util.HttpPayload;
import net.hycrafthd.minecraft_authenticator.util.HttpResponse;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil.TimeoutValues;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticatePayload;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticateResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.ErrorResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.RefreshPayload;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.RefreshResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.ValidatePayload;

public class YggdrasilService {
	
	private static HttpResponse serviceRequest(String endpoint, String payload, TimeoutValues timeoutValues) throws IOException {
		return ConnectionUtil.jsonPostRequest(ConnectionUtil.urlBuilder(Constants.YGGDRASIL_SERVICE, endpoint), HttpPayload.fromString(payload), timeoutValues);
	}
	
	private static <T> YggdrasilResponse<T> responseServiceRequest(String endpoint, Object payload, Class<T> responseClass, TimeoutValues timeoutValues) {
		final String payloadString = Constants.GSON.toJson(payload);
		
		final String responseString;
		try {
			responseString = serviceRequest(endpoint, payloadString, timeoutValues).getAsString();
		} catch (IOException ex) {
			return YggdrasilResponse.ofException(ex);
		}
		
		final Optional<ErrorResponse> errorResponse = findError(responseString);
		if (errorResponse.isPresent()) {
			return YggdrasilResponse.ofError(errorResponse.get());
		}
		
		final T response = Constants.GSON.fromJson(responseString, responseClass);
		return YggdrasilResponse.ofResponse(response);
	}
	
	private static Optional<ErrorResponse> findError(String responseString) {
		final JsonElement element = JsonParser.parseString(responseString);
		if (element.isJsonObject() && element.getAsJsonObject().get("error") != null) {
			return Optional.of(Constants.GSON.fromJson(responseString, ErrorResponse.class));
		} else {
			return Optional.empty();
		}
	}
	
	public static YggdrasilResponse<AuthenticateResponse> authenticate(AuthenticatePayload payload, TimeoutValues timeoutValues) {
		return responseServiceRequest(Constants.YGGDRASIL_ENDPOINT_AUTHENTICATE, payload, AuthenticateResponse.class, timeoutValues);
	}
	
	public static YggdrasilResponse<RefreshResponse> refresh(RefreshPayload payload, TimeoutValues timeoutValues) {
		return responseServiceRequest(Constants.YGGDRASIL_ENDPOINT_REFRESH, payload, RefreshResponse.class, timeoutValues);
	}
	
	public static YggdrasilResponse<Boolean> validate(ValidatePayload payload, TimeoutValues timeoutValues) {
		final String payloadString = Constants.GSON.toJson(payload);
		
		final String responseString;
		try {
			final HttpResponse response = serviceRequest(Constants.YGGDRASIL_ENDPOINT_VALIDATE, payloadString, timeoutValues);
			responseString = response.getAsString();
			if (response.getResponseCode() == 204) {
				return YggdrasilResponse.ofResponse(true);
			}
		} catch (IOException ex) {
			return YggdrasilResponse.ofException(ex);
		}
		
		final Optional<ErrorResponse> errorResponse = findError(responseString);
		if (errorResponse.isPresent()) {
			return YggdrasilResponse.ofError(errorResponse.get());
		}
		return YggdrasilResponse.ofResponse(false);
	}
	
}
