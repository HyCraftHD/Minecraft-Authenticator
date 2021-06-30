package net.hycrafthd.minecraft_authenticator.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftAuthenticationException;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthErrorResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthTokenResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;
import net.hycrafthd.minecraft_authenticator.yggdrasil.YggdrasilAuthenticationException;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticatePayload;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticatePayload.Agent;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticateResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.service.YggdrasilResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.service.YggdrasilService;

public class AuthenticationUtil {
	
	public static void writeAuthenticationFile(AuthenticationFile authFile, Path path) throws IOException {
		final JsonElement element = Constants.GSON.toJsonTree(authFile);
		if (element.isJsonObject()) {
			element.getAsJsonObject().addProperty("warning", Constants.FILE_WARNING);
		}
		final String json = Constants.GSON_PRETTY.toJson(element);
		Files.write(path, json.getBytes(StandardCharsets.UTF_8));
	}
	
	public static AuthenticationFile readAuthenticationFile(Path path) throws IOException {
		try {
			final String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
			return Constants.GSON.fromJson(json, AuthenticationFile.class);
		} catch (JsonParseException ex) {
			throw new IOException("Cannot parse authentication file", ex);
		}
	}
	
	public static AuthenticationFile.Microsoft createMicrosoftAuthenticationFile(Optional<Entry<String, String>> customAzureApplication, String authorizationCode) throws MicrosoftAuthenticationException {
		final MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> microsoftResponse;
		if (customAzureApplication.isPresent()) {
			final Entry<String, String> entry = customAzureApplication.get();
			final String clientId = entry.getKey();
			final String redirectUrl = entry.getValue();
			microsoftResponse = MicrosoftService.oAuthTokenFromCode(clientId, redirectUrl, authorizationCode);
		} else {
			microsoftResponse = MicrosoftService.oAuthTokenFromCode(authorizationCode);
		}
		
		if (microsoftResponse.hasException()) {
			throw new MicrosoftAuthenticationException("Cannot get oAuth token", microsoftResponse.getException().get());
		} else if (microsoftResponse.hasErrorResponse()) {
			throw new MicrosoftAuthenticationException("Cannot get oAuth token because: " + microsoftResponse.getErrorResponse().get());
		}
		final OAuthTokenResponse response = microsoftResponse.getResponse().get();
		return new AuthenticationFile.Microsoft(response.getRefreshToken());
	}
	
	public static AuthenticationFile.Yggdrasil createYggdrasilAuthenticationFile(String clientToken, String username, String password) throws YggdrasilAuthenticationException {
		final YggdrasilResponse<AuthenticateResponse> yggdrasilResponse = YggdrasilService.authenticate(new AuthenticatePayload(new Agent("Minecraft", 1), username, password, clientToken, true));
		if (yggdrasilResponse.hasException()) {
			throw new YggdrasilAuthenticationException("Cannot authenticate minecraft account", yggdrasilResponse.getException().get());
		} else if (yggdrasilResponse.hasErrorResponse()) {
			throw new YggdrasilAuthenticationException("Cannot authenticate minecraft account because: " + yggdrasilResponse.getErrorResponse().get());
		}
		final AuthenticateResponse response = yggdrasilResponse.getResponse().get();
		return new AuthenticationFile.Yggdrasil(response.getAccessToken(), response.getClientToken());
	}
}
