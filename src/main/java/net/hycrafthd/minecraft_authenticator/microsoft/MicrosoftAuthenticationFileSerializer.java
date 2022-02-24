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
		
		final JsonObject extraPropertiesObject = new JsonObject();
		authFile.getExtraProperties().forEach(extraPropertiesObject::addProperty);
		object.add("extraProperties", extraPropertiesObject);
		
		return object;
	}
	
	public static MicrosoftAuthenticationFile deserialize(JsonObject object) {
		final JsonObject clientIdObject = object.getAsJsonObject("clientId");
		final long clientIdMost = clientIdObject.get("most").getAsLong();
		final long clientIdLeast = clientIdObject.get("least").getAsLong();
		final UUID clientId = new UUID(clientIdMost, clientIdLeast);
		
		final String refreshToken = object.get("refreshToken").getAsString();
		
		final MicrosoftAuthenticationFile file = new MicrosoftAuthenticationFile(clientId, refreshToken);
		
		final JsonObject extraPropertiesObject = object.getAsJsonObject("extraProperties");
		if (extraPropertiesObject != null) {
			extraPropertiesObject.keySet().forEach(key -> {
				file.getExtraProperties().put(key, extraPropertiesObject.get(key).getAsString());
			});
		}
		
		return file;
	}
	
}
