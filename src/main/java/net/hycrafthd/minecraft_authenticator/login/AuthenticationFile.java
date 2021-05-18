package net.hycrafthd.minecraft_authenticator.login;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile.AuthenticationFileDeserializer;

@JsonAdapter(AuthenticationFileDeserializer.class)
public class AuthenticationFile {
	
	private final Type type;
	
	public AuthenticationFile(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "AuthenticationFile [type=" + type + "]";
	}
	
	public static enum Type {
		@SerializedName("yggdrasil")
		YGGDRASIL,
		@SerializedName("microsoft")
		MICROSOFT;
	}
	
	public static class YggdrasilAuthenticationFile extends AuthenticationFile {
		
		private final String accessToken;
		private final String clientToken;
		
		public YggdrasilAuthenticationFile(String accessToken, String clientToken) {
			super(Type.YGGDRASIL);
			this.accessToken = accessToken;
			this.clientToken = clientToken;
		}
		
		public String getAccessToken() {
			return accessToken;
		}
		
		public String getClientToken() {
			return clientToken;
		}
		
		@Override
		public String toString() {
			return "YggdrasilAuthenticationFile [accessToken=" + accessToken + ", clientToken=" + clientToken + ", toString()=" + super.toString() + "]";
		}
		
	}
	
	public static class MicrosoftAuthenticationFile extends AuthenticationFile {
		
		private final String refreshToken;
		
		public MicrosoftAuthenticationFile(String refreshToken) {
			super(Type.MICROSOFT);
			this.refreshToken = refreshToken;
		}
		
		public String getRefreshToken() {
			return refreshToken;
		}
		
		@Override
		public String toString() {
			return "MicrosoftAuthenticationFile [refreshToken=" + refreshToken + ", toString()=" + super.toString() + "]";
		}
		
	}
	
	public class AuthenticationFileDeserializer implements JsonDeserializer<AuthenticationFile> {
		
		@Override
		public AuthenticationFile deserialize(JsonElement json, java.lang.reflect.Type typeOf, JsonDeserializationContext context) throws JsonParseException {
			final JsonObject object = json.getAsJsonObject();
			final Type type = context.deserialize(object.get("type"), Type.class);
			if (type == Type.YGGDRASIL) {
				return new YggdrasilAuthenticationFile(object.get("accessToken").getAsString(), object.get("clientToken").getAsString());
			} else if (type == Type.MICROSOFT) {
				return new MicrosoftAuthenticationFile(object.get("refreshToken").getAsString());
			}
			throw new JsonParseException("Type must be 'yggdrasil' or 'microsoft'");
		}
	}
}
