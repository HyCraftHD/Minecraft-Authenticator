package net.hycrafthd.minecraft_authenticator.microsoft.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.microsoft.api.MinecraftHasPurchasedResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.MinecraftLoginWithXBoxPayload;
import net.hycrafthd.minecraft_authenticator.microsoft.api.MinecraftLoginWithXBoxResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.MinecraftProfileResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthErrorResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthTokenResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XBLAuthenticatePayload;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XBLAuthenticateResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XSTSAuthorizeErrorResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XSTSAuthorizePayload;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XSTSAuthorizeResponse;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil;
import net.hycrafthd.minecraft_authenticator.util.HttpPayload;
import net.hycrafthd.minecraft_authenticator.util.HttpResponse;
import net.hycrafthd.minecraft_authenticator.util.Parameters;

public class MicrosoftService {
	
	private static MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> oAuthResponseServiceRequest(Parameters parameters) {
		final String responseString;
		try {
			responseString = ConnectionUtil.urlEncodedPostRequest(ConnectionUtil.urlBuilder(Constants.MICROSOFT_OAUTH_SERVICE, Constants.MICROSOFT_OAUTH_ENDPOINT_TOKEN), ConnectionUtil.JSON_CONTENT_TYPE, parameters).getAsString();
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
	
	public static URL oAuthLoginUrl() {
		return oAuthLoginUrl(Constants.MICROSOFT_CLIENT_ID, Constants.MICROSOFT_OAUTH_REDIRECT_URL);
	}
	
	public static URL oAuthLoginUrl(String clientId, String redirectUrl) {
		final Parameters parameters = Parameters.create() //
				.add("client_id", clientId) //
				.add("response_type", "code") //
				.add("scope", "XboxLive.signin offline_access") //
				.add("redirect_uri", redirectUrl);
		
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
	
	public static MicrosoftResponse<XBLAuthenticateResponse, Integer> xblAuthenticate(XBLAuthenticatePayload payload) {
		final String responseString;
		try {
			final HttpResponse response = ConnectionUtil.jsonPostRequest(ConnectionUtil.urlBuilder(Constants.MICROSOFT_XBL_AUTHENTICATE_URL), HttpPayload.fromString(Constants.GSON.toJson(payload)));
			responseString = response.getAsString();
			if (response.getResponseCode() >= 300) {
				return MicrosoftResponse.ofError(response.getResponseCode());
			}
		} catch (IOException ex) {
			return MicrosoftResponse.ofException(ex);
		}
		final XBLAuthenticateResponse response = Constants.GSON.fromJson(responseString, XBLAuthenticateResponse.class);
		return MicrosoftResponse.ofResponse(response);
	}
	
	public static MicrosoftResponse<XSTSAuthorizeResponse, XSTSAuthorizeErrorResponse> xstsAuthorize(XSTSAuthorizePayload payload) {
		final String responseString;
		try {
			responseString = ConnectionUtil.jsonPostRequest(ConnectionUtil.urlBuilder(Constants.MICROSOFT_XSTS_AUTHORIZE_URL), HttpPayload.fromString(Constants.GSON.toJson(payload))).getAsString();
		} catch (IOException ex) {
			return MicrosoftResponse.ofException(ex);
		}
		
		final JsonElement element = JsonParser.parseString(responseString);
		if (element.isJsonObject() && element.getAsJsonObject().get("XErr") != null) {
			final XSTSAuthorizeErrorResponse response = Constants.GSON.fromJson(responseString, XSTSAuthorizeErrorResponse.class);
			return MicrosoftResponse.ofError(response);
		}
		
		final XSTSAuthorizeResponse response = Constants.GSON.fromJson(responseString, XSTSAuthorizeResponse.class);
		return MicrosoftResponse.ofResponse(response);
	}
	
	public static MicrosoftResponse<MinecraftLoginWithXBoxResponse, Integer> minecraftLoginWithXsts(MinecraftLoginWithXBoxPayload payload) {
		final String responseString;
		try {
			final HttpResponse response = ConnectionUtil.jsonPostRequest(ConnectionUtil.urlBuilder(Constants.MICROSOFT_MINECRAFT_SERVICE, Constants.MICROSOFT_MINECRAFT_ENDPOINT_XBOX_LOGIN), HttpPayload.fromGson(payload));
			responseString = response.getAsString();
			if (response.getResponseCode() >= 300) {
				return MicrosoftResponse.ofError(response.getResponseCode());
			}
		} catch (IOException ex) {
			return MicrosoftResponse.ofException(ex);
		}
		
		final MinecraftLoginWithXBoxResponse response = Constants.GSON.fromJson(responseString, MinecraftLoginWithXBoxResponse.class);
		return MicrosoftResponse.ofResponse(response);
	}
	
	public static MicrosoftResponse<MinecraftHasPurchasedResponse, Integer> minecraftHasPurchased(String accessToken) {
		final String responseString;
		try {
			final HttpResponse response = ConnectionUtil.bearerAuthorizationJsonGetRequest(ConnectionUtil.urlBuilder(Constants.MICROSOFT_MINECRAFT_SERVICE, Constants.MICROSOFT_MINECRAFT_ENDPOINT_HAS_PURCHASED), accessToken);
			responseString = response.getAsString();
			if (response.getResponseCode() >= 300) {
				return MicrosoftResponse.ofError(response.getResponseCode());
			}
		} catch (IOException ex) {
			return MicrosoftResponse.ofException(ex);
		}
		
		final MinecraftHasPurchasedResponse response = Constants.GSON.fromJson(responseString, MinecraftHasPurchasedResponse.class);
		return MicrosoftResponse.ofResponse(response);
	}
	
	public static MicrosoftResponse<MinecraftProfileResponse, Integer> minecraftProfile(String accessToken) {
		final String responseString;
		try {
			final HttpResponse response = ConnectionUtil.bearerAuthorizationJsonGetRequest(ConnectionUtil.urlBuilder(Constants.MICROSOFT_MINECRAFT_SERVICE, Constants.MICROSOFT_MINECRAFT_ENDPOINT_PROFILE), accessToken);
			responseString = response.getAsString();
			if (response.getResponseCode() >= 300) {
				return MicrosoftResponse.ofError(response.getResponseCode());
			}
		} catch (IOException ex) {
			return MicrosoftResponse.ofException(ex);
		}
		
		final MinecraftProfileResponse response = Constants.GSON.fromJson(responseString, MinecraftProfileResponse.class);
		return MicrosoftResponse.ofResponse(response);
	}
	
}
