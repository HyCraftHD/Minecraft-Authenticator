package net.hycrafthd.minecraft_authenticator.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftAuthenticationFile;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftAuthenticationFileSerializer;

public class AuthenticationFileUtil {
	
	// File save and read methods
	
	public static void toCompressedOutputStream(AuthenticationFile authFile, OutputStream outputStream) throws IOException {
		outputStream.write(toCompressedBytes(authFile));
	}
	
	public static byte[] toCompressedBytes(AuthenticationFile authFile) throws IOException {
		final String json = toString(authFile);
		final byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
		
		try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); //
				final GZIPOutputStream gZipOutputStream = new GZIPOutputStream(outputStream)) {
			gZipOutputStream.write(bytes);
			gZipOutputStream.finish();
			return outputStream.toByteArray();
		}
	}
	
	public static String toString(AuthenticationFile authFile) {
		final JsonObject object = toJson(authFile);
		
		return Constants.GSON.toJson(object);
	}
	
	public static JsonObject toJson(AuthenticationFile authFile) {
		final JsonObject object = new JsonObject();
		
		object.addProperty("warning", Constants.FILE_WARNING);
		object.add("file", serializeTypes(authFile));
		
		return object;
	}
	
	public static AuthenticationFile fromCompressedInputStream(InputStream inputStream) throws IOException {
		return fromCompressedBytes(inputStream.readAllBytes());
	}
	
	public static AuthenticationFile fromCompressedBytes(byte[] compressedBytes) throws IOException {
		final byte[] bytes;
		
		try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedBytes); //
				final GZIPInputStream gZipInputStream = new GZIPInputStream(inputStream)) {
			bytes = gZipInputStream.readAllBytes();
		}
		
		final String json = new String(bytes, StandardCharsets.UTF_8);
		return fromString(json);
	}
	
	public static AuthenticationFile fromString(String json) throws IOException {
		final JsonObject object;
		try {
			object = JsonParser.parseString(json).getAsJsonObject();
		} catch (final JsonParseException | IllegalStateException | ClassCastException ex) {
			throw new IOException("Cannot parse authentication file", ex);
		}
		
		return fromJson(object);
	}
	
	public static AuthenticationFile fromJson(JsonObject object) throws IOException {
		try {
			return deserializeTypes(object.get("file"));
		} catch (final JsonParseException | IllegalStateException | ClassCastException ex) {
			throw new IOException("Cannot parse authentication file", ex);
		}
	}
	
	// Serialize / Deserialize the special authentication file types
	
	private static JsonElement serializeTypes(AuthenticationFile authFile) {
		if (authFile instanceof MicrosoftAuthenticationFile microsoftFile) {
			final JsonObject jsonObject = MicrosoftAuthenticationFileSerializer.serialize(microsoftFile);
			jsonObject.addProperty("type", "microsoft");
			return jsonObject;
		} else {
			throw new IllegalStateException("AuthenticationFile must be a microsoft authentication file");
		}
	}
	
	private static AuthenticationFile deserializeTypes(JsonElement jsonElement) {
		final JsonObject jsonObject = jsonElement.getAsJsonObject();
		final String type = jsonObject.get("type").getAsString();
		
		if ("microsoft".equals(type)) {
			return MicrosoftAuthenticationFileSerializer.deserialize(jsonObject);
		} else {
			throw new IllegalStateException("AuthenticationFile must be a microsoft authentication file. Type '" + type + "' is not supported");
		}
	}
}
