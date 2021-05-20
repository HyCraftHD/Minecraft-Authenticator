package net.hycrafthd.minecraft_authenticator.login;

import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile.AuthenticationFileDeserializer;
import net.hycrafthd.minecraft_authenticator.util.AuthenticationUtil;

/**
 * File that contains authentication information. This is currently either a {@link AuthenticationFile.Yggdrasil} or a
 * {@link AuthenticationFile.Microsoft} instance.
 */
@JsonAdapter(AuthenticationFileDeserializer.class)
public abstract class AuthenticationFile {
	
	/**
	 * Read an {@link AuthenticationFile} from a path and returns an {@link AuthenticationFile} if file was found, readable
	 * and a valid {@link AuthenticationFile}.
	 * 
	 * @param path Path to read the file
	 * @return An {@link AuthenticationFile} instance
	 * @throws IOException Errors if file is not found, readable or parsable
	 */
	public static AuthenticationFile read(Path path) throws IOException {
		return AuthenticationUtil.readAuthenticationFile(path);
	}
	
	private final Type type;
	
	private AuthenticationFile(Type type) {
		this.type = type;
	}
	
	/**
	 * Write this {@link AuthenticationFile} to a file if the file can be created and or is writable.
	 * <p>
	 * Attention: This file is in plain text and can be read by anyone that has this file. Even though this file does not
	 * contain any credentials, this file contains tokens for refreshing your minecraft session that should be kept private!
	 * </p>
	 * 
	 * @param path Path to write the file
	 * @throws IOException Errors if file could not be created or written
	 */
	public void write(Path path) throws IOException {
		AuthenticationUtil.writeAuthenticationFile(this, path);
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
	
	public static class Yggdrasil extends AuthenticationFile {
		
		private final String accessToken;
		private final String clientToken;
		
		public Yggdrasil(String accessToken, String clientToken) {
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
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
			result = prime * result + ((clientToken == null) ? 0 : clientToken.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object object) {
			if (this == object)
				return true;
			if (object == null)
				return false;
			if (getClass() != object.getClass())
				return false;
			final Yggdrasil other = (Yggdrasil) object;
			if (accessToken == null) {
				if (other.accessToken != null)
					return false;
			} else if (!accessToken.equals(other.accessToken))
				return false;
			if (clientToken == null) {
				if (other.clientToken != null)
					return false;
			} else if (!clientToken.equals(other.clientToken))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "YggdrasilAuthenticationFile [accessToken=" + accessToken + ", clientToken=" + clientToken + ", toString()=" + super.toString() + "]";
		}
		
	}
	
	public static class Microsoft extends AuthenticationFile {
		
		private final String refreshToken;
		
		public Microsoft(String refreshToken) {
			super(Type.MICROSOFT);
			this.refreshToken = refreshToken;
		}
		
		public String getRefreshToken() {
			return refreshToken;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((refreshToken == null) ? 0 : refreshToken.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object object) {
			if (this == object)
				return true;
			if (object == null)
				return false;
			if (getClass() != object.getClass())
				return false;
			final Microsoft other = (Microsoft) object;
			if (refreshToken == null) {
				if (other.refreshToken != null)
					return false;
			} else if (!refreshToken.equals(other.refreshToken))
				return false;
			return true;
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
				return new Yggdrasil(object.get("accessToken").getAsString(), object.get("clientToken").getAsString());
			} else if (type == Type.MICROSOFT) {
				return new Microsoft(object.get("refreshToken").getAsString());
			}
			throw new JsonParseException("Type must be 'yggdrasil' or 'microsoft'");
		}
	}
}
