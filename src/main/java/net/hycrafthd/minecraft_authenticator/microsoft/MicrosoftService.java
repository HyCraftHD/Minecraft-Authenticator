package net.hycrafthd.minecraft_authenticator.microsoft;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthErrorResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthTokenResponse;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil;
import net.hycrafthd.minecraft_authenticator.util.Parameters;

public class MicrosoftService {
	
	public static MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> oAuthResponseServiceRequest(Parameters parameters) {
		final String responseString;
		try {
			responseString = ConnectionUtil.urlEncodedRequest(ConnectionUtil.urlBuilder(Constants.MICROSOFT_OAUTH_SERVICE, Constants.MICROSOFT_OAUTH_ENDPOINT_TOKEN), parameters).getAsString();
		} catch (IOException ex) {
			return MicrosoftResponse.ofException(ex);
		}
		
		final Optional<OAuthErrorResponse> errorResponse = findOAuthError(responseString);
		if (errorResponse.isPresent()) {
			return MicrosoftResponse.ofError(errorResponse.get());
		}
		
		final OAuthTokenResponse response = Constants.GSON.fromJson(responseString, OAuthTokenResponse.class);
		return MicrosoftResponse.ofResponse(response);
	}
	
	private static Optional<OAuthErrorResponse> findOAuthError(String responseString) {
		final JsonElement element = JsonParser.parseString(responseString);
		if (element.isJsonObject() && element.getAsJsonObject().get("error") != null) {
			return Optional.of(Constants.GSON.fromJson(responseString, OAuthErrorResponse.class));
		} else {
			return Optional.empty();
		}
	}
	
	public static URL generateOAuthLoginUrl() {
		final Parameters parameters = Parameters.create() //
				.add("client_id", Constants.MICROSOFT_CLIENT_ID) //
				.add("response_type", "code") //
				.add("scope", "XboxLive.signin offline_access") //
				.add("redirect_uri", Constants.MICROSOFT_OAUTH_REDIRECT_URL);
		
		try {
			return ConnectionUtil.urlBuilder(Constants.MICROSOFT_OAUTH_SERVICE, Constants.MICROSOFT_OAUTH_ENDPOINT_AUTHORIZE, parameters);
		} catch (MalformedURLException ex) {
			throw new AssertionError("This url should never be malformed.");
		}
	}
	
	public static MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> oAuthTokenFromCode(String authorizationCode) {
		final Parameters parameters = Parameters.create() //
				.add("client_id", Constants.MICROSOFT_CLIENT_ID) //
				.add("code", authorizationCode) //
				.add("grant_type", "authorization_code") //
				.add("redirect_uri", Constants.MICROSOFT_OAUTH_REDIRECT_URL);
		
		return oAuthResponseServiceRequest(parameters);
	}
	
	public static MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> oAuthTokenFromRefreshToken(String refreshToken) {
		final Parameters parameters = Parameters.create() //
				.add("client_id", Constants.MICROSOFT_CLIENT_ID) //
				.add("refresh_token", refreshToken) //
				.add("grant_type", "refresh_token") //
				.add("redirect_uri", Constants.MICROSOFT_OAUTH_REDIRECT_URL);
		
		return oAuthResponseServiceRequest(parameters);
		
	}
	
}
