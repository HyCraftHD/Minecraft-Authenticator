package net.hycrafthd.minecraft_authenticator.login.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import net.hycrafthd.minecraft_authenticator.login.file.AuthenticationFile.AuthenticationFileDeserializer;
import net.hycrafthd.minecraft_authenticator.util.AuthenticationUtil;

/**
 * File that contains authentication information. This is currently either a {@link YggdrasilAuthenticationFile} or a
 * {@link MicrosoftAuthenticationFile} instance.
 */
@JsonAdapter(AuthenticationFileDeserializer.class)
public sealed abstract class AuthenticationFile permits YggdrasilAuthenticationFile,MicrosoftAuthenticationFile {
	
	/**
	 * Reads an {@link AuthenticationFile} from an input stream.
	 * 
	 * @param inputStream InputStream to read the data from
	 * @return An {@link AuthenticationFile} instance
	 * @throws IOException Error if data could not be parsed
	 */
	public static AuthenticationFile read(InputStream inputStream) throws IOException {
		return AuthenticationUtil.readAuthenticationFile(inputStream);
	}
	
	private final Type type;
	
	protected AuthenticationFile(Type type) {
		this.type = type;
	}
	
	/**
	 * Write this {@link AuthenticationFile} to an output stream.
	 * <p>
	 * Attention: The data is in plain text and can be read by anyone that has access to the output stream data (e.g.
	 * writing to a file). Even though this data does not contain any credentials, it contains tokens for refreshing your
	 * minecraft session that should be kept private!
	 * </p>
	 * 
	 * @param outputStream Output stream to write the {@link AuthenticationFile} to
	 * @throws IOException Errors if output stream is not writable
	 */
	public void write(OutputStream outputStream) throws IOException {
		AuthenticationUtil.writeAuthenticationFile(this, outputStream);
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
