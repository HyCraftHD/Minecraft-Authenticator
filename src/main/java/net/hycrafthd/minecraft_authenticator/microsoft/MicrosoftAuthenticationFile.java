package net.hycrafthd.minecraft_authenticator.microsoft;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;

public class MicrosoftAuthenticationFile extends AuthenticationFile {
	
	private final String refreshToken;
	
	public MicrosoftAuthenticationFile(UUID clientId, String refreshToken) {
		super(Type.MICROSOFT, clientId);
		this.refreshToken = refreshToken;
	}
	
	/**
	 * The refresh token for microsoft oAuth
	 *
	 * @return Refresh token
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	
	@Override
	public String toString() {
		return "MicrosoftAuthenticationFile [refreshToken=" + refreshToken + "]";
	}
	
	public static class MicrosoftAuthenticationFileDeserializer implements JsonDeserializer<MicrosoftAuthenticationFile>, JsonSerializer<MicrosoftAuthenticationFile> {
		
		public static final MicrosoftAuthenticationFileDeserializer INSTANCE = new MicrosoftAuthenticationFileDeserializer();
		
		@Override
		public MicrosoftAuthenticationFile deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			final JsonObject object = json.getAsJsonObject();
			
			final JsonObject clientIdObject = object.getAsJsonObject("clientId");
			final long clientIdMost = clientIdObject.get("most").getAsLong();
			final long clientIdLeast = clientIdObject.get("least").getAsLong();
			final UUID clientId = new UUID(clientIdMost, clientIdLeast);
			
			final String encodedRefreshToken = object.get("refreshToken").getAsString();
			final byte[] rawBytes = encodedRefreshToken.getBytes(StandardCharsets.UTF_8);
			final byte[] decodedBytes = Base64.getDecoder().decode(rawBytes);
			final String refreshToken = new String(decodedBytes, StandardCharsets.UTF_8);
			
			return new MicrosoftAuthenticationFile(clientId, refreshToken);
		}
		
		@Override
		public JsonElement serialize(MicrosoftAuthenticationFile src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject object = new JsonObject();
			
			object.add("type", context.serialize(src.getType(), Type.class));
			
			final JsonObject clientIdObject = new JsonObject();
			clientIdObject.addProperty("most", src.getClientId().getMostSignificantBits());
			clientIdObject.addProperty("least", src.getClientId().getLeastSignificantBits());
			object.add("clientId", clientIdObject);
			
			final byte[] rawBytes = src.refreshToken.getBytes(StandardCharsets.UTF_8);
			final byte[] encodedBytes = Base64.getEncoder().encode(rawBytes);
			object.addProperty("refreshToken", new String(encodedBytes, StandardCharsets.UTF_8));
			
			return object;
		}
	}
}
