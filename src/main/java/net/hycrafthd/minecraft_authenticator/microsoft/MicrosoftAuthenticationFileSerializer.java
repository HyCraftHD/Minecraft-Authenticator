package net.hycrafthd.minecraft_authenticator.microsoft;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import com.google.gson.JsonObject;

public class MicrosoftAuthenticationFileSerializer {
	
	public static JsonObject serialize(MicrosoftAuthenticationFile authFile) {
		final JsonObject object = new JsonObject();
		
		final JsonObject clientIdObject = new JsonObject();
		clientIdObject.addProperty("most", authFile.getClientId().getMostSignificantBits());
		clientIdObject.addProperty("least", authFile.getClientId().getLeastSignificantBits());
		object.add("clientId", clientIdObject);
		
		final byte[] rawBytes = authFile.getRefreshToken().getBytes(StandardCharsets.UTF_8);
		final byte[] encodedBytes = Base64.getEncoder().encode(rawBytes);
		object.addProperty("refreshToken", new String(encodedBytes, StandardCharsets.UTF_8));
		
		return object;
	}
	
	public static MicrosoftAuthenticationFile deserialize(JsonObject object) {
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
	
}
