package net.hycrafthd.minecraft_authenticator.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile.AuthenticationFileDeserializer;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftAuthenticationFile;
import net.hycrafthd.minecraft_authenticator.util.AuthenticationUtil;

/**
 * File that contains authentication information.
 */
@JsonAdapter(AuthenticationFileDeserializer.class)
public abstract class AuthenticationFile {
	
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
	
	public static class AuthenticationFileDeserializer implements JsonDeserializer<AuthenticationFile> {
		
		@Override
		public AuthenticationFile deserialize(JsonElement json, Type typeOf, JsonDeserializationContext context) throws JsonParseException {
			return new MicrosoftAuthenticationFile(json.getAsJsonObject().get("refreshToken").getAsString());
		}
	}
}
