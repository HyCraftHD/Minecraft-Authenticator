package net.hycrafthd.minecraft_authenticator.login;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import net.hycrafthd.minecraft_authenticator.Constants;

public class Authenticator {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
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
	
}
