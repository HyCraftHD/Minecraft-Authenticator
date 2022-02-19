package net.hycrafthd.minecraft_authenticator.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftAuthenticationException;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftAuthenticationFile;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthErrorResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthTokenResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil.TimeoutValues;

public class AuthenticationUtil {
	
	public static void writeAuthenticationFile(AuthenticationFile authFile, OutputStream outputStream) throws IOException {
		outputStream.write(writeAuthenticationFile(authFile));
	}
	
	public static byte[] writeAuthenticationFile(AuthenticationFile authFile) {
		final JsonElement element = Constants.GSON.toJsonTree(authFile);
		if (element.isJsonObject()) {
			element.getAsJsonObject().addProperty("warning", Constants.FILE_WARNING);
		}
		final String json = Constants.GSON.toJson(element);
		return json.getBytes(StandardCharsets.UTF_8);
	}
	
	public static AuthenticationFile readAuthenticationFile(InputStream inputStream) throws IOException {
		return readAuthenticationFile(inputStream.readAllBytes());
	}
	
	public static AuthenticationFile readAuthenticationFile(byte[] bytes) throws IOException {
		try {
			final String json = new String(bytes, StandardCharsets.UTF_8);
			return Constants.GSON.fromJson(json, AuthenticationFile.class);
		} catch (final JsonParseException | IllegalStateException | ClassCastException ex) {
			throw new IOException("Cannot parse authentication file", ex);
		}
	}
	
	public static MicrosoftAuthenticationFile createMicrosoftAuthenticationFile(Optional<Entry<String, String>> customAzureApplication, String authorizationCode, TimeoutValues timeoutValues) throws MicrosoftAuthenticationException {
		final MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> microsoftResponse;
		if (customAzureApplication.isPresent()) {
			final Entry<String, String> entry = customAzureApplication.get();
			final String clientId = entry.getKey();
			final String redirectUrl = entry.getValue();
			microsoftResponse = MicrosoftService.oAuthTokenFromCode(clientId, redirectUrl, authorizationCode, timeoutValues);
		} else {
			microsoftResponse = MicrosoftService.oAuthTokenFromCode(authorizationCode, timeoutValues);
		}
		
		if (microsoftResponse.hasException()) {
			throw new MicrosoftAuthenticationException("Cannot get oAuth token", microsoftResponse.getException().get());
		} else if (microsoftResponse.hasErrorResponse()) {
			throw new MicrosoftAuthenticationException("Cannot get oAuth token because: " + microsoftResponse.getErrorResponse().get());
		}
		final OAuthTokenResponse response = microsoftResponse.getResponse().get();
		return new MicrosoftAuthenticationFile(UUID.randomUUID(), response.getRefreshToken());
	}
}
