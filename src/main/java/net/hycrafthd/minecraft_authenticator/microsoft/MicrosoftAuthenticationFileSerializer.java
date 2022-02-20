package net.hycrafthd.minecraft_authenticator.microsoft;

import java.util.UUID;

import com.google.gson.JsonObject;

public class MicrosoftAuthenticationFileSerializer {
	
	public static JsonObject serialize(MicrosoftAuthenticationFile authFile) {
		final JsonObject object = new JsonObject();
		
		final JsonObject clientIdObject = new JsonObject();
		clientIdObject.addProperty("most", authFile.getClientId().getMostSignificantBits());
		clientIdObject.addProperty("least", authFile.getClientId().getLeastSignificantBits());
		object.add("clientId", clientIdObject);
		
		object.addProperty("refreshToken", authFile.getRefreshToken());
		
		return object;
	}
	
	public static MicrosoftAuthenticationFile deserialize(JsonObject object) {
		final JsonObject clientIdObject = object.getAsJsonObject("clientId");
		final long clientIdMost = clientIdObject.get("most").getAsLong();
		final long clientIdLeast = clientIdObject.get("least").getAsLong();
		final UUID clientId = new UUID(clientIdMost, clientIdLeast);
		
		final String refreshToken = object.get("refreshToken").getAsString();
		
		return new MicrosoftAuthenticationFile(clientId, refreshToken);
	}
	
}
