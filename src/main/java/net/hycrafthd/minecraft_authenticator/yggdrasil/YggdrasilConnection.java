package net.hycrafthd.minecraft_authenticator.yggdrasil;

import java.io.IOException;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil;
import net.hycrafthd.minecraft_authenticator.util.HttpPayload;
import net.hycrafthd.minecraft_authenticator.util.HttpResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticatePayload;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticateResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.ErrorResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.RefreshPayload;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.RefreshResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.ValidatePayload;

public class YggdrasilConnection {
	
	private static final String ENDPOINT_AUTHENTICATE = "authenticate";
	private static final String ENDPOINT_REFRESH = "refresh";
	private static final String ENDPOINT_VALIDATE = "validate";
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	private static HttpResponse request(String endpoint, String payload) throws IOException {
		return ConnectionUtil.jsonRequest(ConnectionUtil.urlBuilder(Constants.YGGDRASIL_SERVICE, endpoint), HttpPayload.fromString(payload));
	}
	
	public static YggdrasilResponse<AuthenticateResponse> authenticate(AuthenticatePayload payload) {
		final String payloadString = GSON.toJson(payload);
		
		final String responseString;
		try {
			responseString = request(ENDPOINT_AUTHENTICATE, payloadString).getAsString();
		} catch (IOException ex) {
			return new YggdrasilResponse<>(ex);
		}
		
		final Optional<ErrorResponse> errorResponse = findError(responseString);
		if (errorResponse.isPresent()) {
			return new YggdrasilResponse<>(errorResponse.get());
		}
		
		final AuthenticateResponse response = GSON.fromJson(responseString, AuthenticateResponse.class);
		return new YggdrasilResponse<>(response);
	}
	
	public static YggdrasilResponse<RefreshResponse> refresh(RefreshPayload payload) {
		final String payloadString = GSON.toJson(payload);
		
		final String responseString;
		try {
			responseString = request(ENDPOINT_REFRESH, payloadString).getAsString();
		} catch (IOException ex) {
			return new YggdrasilResponse<>(ex);
		}
		
		final Optional<ErrorResponse> errorResponse = findError(responseString);
		if (errorResponse.isPresent()) {
			return new YggdrasilResponse<>(errorResponse.get());
		}
		
		final RefreshResponse response = GSON.fromJson(responseString, RefreshResponse.class);
		return new YggdrasilResponse<>(response);
	}
	
	public static YggdrasilResponse<Boolean> validate(ValidatePayload payload) {
		final String payloadString = GSON.toJson(payload);
		
		final String responseString;
		try {
			final HttpResponse response = request(ENDPOINT_VALIDATE, payloadString);
			responseString = response.getAsString();
			if (response.getResponseCode() == 204) {
				return new YggdrasilResponse<>(true);
			}
		} catch (IOException ex) {
			return new YggdrasilResponse<>(ex);
		}
		
		final Optional<ErrorResponse> errorResponse = findError(responseString);
		if (errorResponse.isPresent()) {
			return new YggdrasilResponse<>(errorResponse.get());
		}
		return new YggdrasilResponse<>(false);
	}
	
	private static Optional<ErrorResponse> findError(String responseString) {
		final JsonElement element = JsonParser.parseString(responseString);
		if (element.isJsonObject() && element.getAsJsonObject().get("error") != null) {
			return Optional.of(GSON.fromJson(responseString, ErrorResponse.class));
		} else {
			return Optional.empty();
		}
	}
}
