package net.hycrafthd.minecraft_authenticator.yggdrasil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticatePayload;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticateResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.ErrorResponse;

public class YggdrasilConnection {
	
	private static final String BASE_URL = "https://authserver.mojang.com";
	
	private static final String ENDPOINT_AUTHENTICATE = "authenticate";
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	private static String request(String endpoint, String payload) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) new URL(BASE_URL + "/" + endpoint).openConnection();
		urlConnection.setConnectTimeout(5000);
		urlConnection.setReadTimeout(5000);
		urlConnection.setInstanceFollowRedirects(false);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Content-Type", "application/json");
		urlConnection.setRequestProperty("Charset", "UTF-8");
		urlConnection.addRequestProperty("User-Agent", "Minecraft-Authenticator");
		urlConnection.setDoOutput(true);
		urlConnection.connect();
		
		try (final OutputStream outputStream = urlConnection.getOutputStream()) {
			urlConnection.getOutputStream().write(payload.getBytes(Charsets.UTF_8));
		}
		
		try (final InputStream inputStream = urlConnection.getResponseCode() >= 400 ? urlConnection.getErrorStream() : urlConnection.getInputStream()) {
			return new String(ByteStreams.toByteArray(inputStream), Charsets.UTF_8);
		}
	}
	
	public static YggdrasilResponse<AuthenticateResponse> authenticate(AuthenticatePayload payload) {
		final String payloadString = GSON.toJson(payload);
		
		final String responseString;
		try {
			responseString = request(ENDPOINT_AUTHENTICATE, payloadString);
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
	
	private static Optional<ErrorResponse> findError(String responseString) {
		final JsonElement element = JsonParser.parseString(responseString);
		if (element.isJsonObject() && element.getAsJsonObject().get("error") != null) {
			return Optional.of(GSON.fromJson(responseString, ErrorResponse.class));
		} else {
			return Optional.empty();
		}
	}
}
