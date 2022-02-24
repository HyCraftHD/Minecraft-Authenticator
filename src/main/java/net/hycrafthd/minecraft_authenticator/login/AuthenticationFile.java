package net.hycrafthd.minecraft_authenticator.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.google.gson.JsonObject;

import net.hycrafthd.minecraft_authenticator.util.AuthenticationFileUtil;

/**
 * File that contains authentication information.
 */
public abstract class AuthenticationFile {
	
	private final UUID clientId;
	private final Map<String, String> extraProperties;
	
	protected AuthenticationFile(UUID clientId) {
		this.clientId = clientId;
		extraProperties = new HashMap<>();
	}
	
	/**
	 * The client id that is used for certain requests
	 *
	 * @return Client id
	 */
	public UUID getClientId() {
		return clientId;
	}
	
	/**
	 * Extra properties that could be attached to an authentication file. They are by design mutable and can be used to
	 * attach properties to the authentication file
	 * 
	 * @return Extra properties
	 */
	public Map<String, String> getExtraProperties() {
		return extraProperties;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(clientId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		AuthenticationFile other = (AuthenticationFile) obj;
		return Objects.equals(clientId, other.clientId);
	}
	
	@Override
	public String toString() {
		return "AuthenticationFile [clientId=" + clientId + ", extraProperties=" + extraProperties + "]";
	}
	
	/**
	 * Reads an {@link AuthenticationFile} from an input stream with with gzip compression.. The input stream is not closed.
	 *
	 * @param inputStream InputStream to read the compressed data from
	 * @return An {@link AuthenticationFile} instance
	 * @throws IOException Error if data could not be uncompressed or parsed
	 */
	public static AuthenticationFile readCompressed(InputStream inputStream) throws IOException {
		return AuthenticationFileUtil.fromCompressedInputStream(inputStream);
	}
	
	/**
	 * Reads an {@link AuthenticationFile} from a byte array with with gzip compression.
	 *
	 * @param bytes Compressed byte array of the authentication file
	 * @return An {@link AuthenticationFile} instance
	 * @throws IOException Error if data could not be uncompressed or parsed
	 */
	public static AuthenticationFile readCompressed(byte[] bytes) throws IOException {
		return AuthenticationFileUtil.fromCompressedBytes(bytes);
	}
	
	/**
	 * Reads an {@link AuthenticationFile} from a string.
	 *
	 * @param string Serialized authentication file
	 * @return An {@link AuthenticationFile} instance
	 * @throws IOException Error if data could not be parsed
	 */
	public static AuthenticationFile readString(String string) throws IOException {
		return AuthenticationFileUtil.fromString(string);
	}
	
	/**
	 * Deserialize an {@link AuthenticationFile} from a {@link JsonObject}
	 *
	 * @param object Json object containing the {@link AuthenticationFile} data
	 * @return An {@link AuthenticationFile} instance
	 * @throws IOException Error if data could not be parsed
	 */
	public static AuthenticationFile deserialize(JsonObject object) throws IOException {
		return AuthenticationFileUtil.fromJson(object);
	}
	
	/**
	 * Write this {@link AuthenticationFile} to an output stream with gzip compression. The output stream is not closed.
	 * <p>
	 * Attention: The data is in plain text and can be read by anyone that has access to the output stream data (e.g.
	 * writing to a file). Even though this data does not contain any credentials, it contains tokens for refreshing your
	 * minecraft session that should be kept private!
	 * </p>
	 *
	 * @param outputStream Output stream to write the compressed {@link AuthenticationFile} to
	 * @throws IOException Error if data could be be compressed
	 */
	public void writeCompressed(OutputStream outputStream) throws IOException {
		AuthenticationFileUtil.toCompressedOutputStream(this, outputStream);
	}
	
	/**
	 * Write this {@link AuthenticationFile} to a byte array with gzip compression.
	 * <p>
	 * Attention: The data is in plain text and can be read by anyone that has access to the byte array data (e.g. writing
	 * to a file). Even though this data does not contain any credentials, it contains tokens for refreshing your minecraft
	 * session that should be kept private!
	 * </p>
	 *
	 * @return Compressed byte array of the authentication file
	 * @throws IOException Error if data could be be compressed
	 */
	public byte[] writeCompressed() throws IOException {
		return AuthenticationFileUtil.toCompressedBytes(this);
	}
	
	/**
	 * Write this {@link AuthenticationFile} to a string.
	 * <p>
	 * Attention: The data is in plain text and can be read by anyone that has access to the byte array data (e.g. writing
	 * to a file). Even though this data does not contain any credentials, it contains tokens for refreshing your minecraft
	 * session that should be kept private!
	 * </p>
	 *
	 * @return Serialized string of the authentication file
	 */
	public String writeString() {
		return AuthenticationFileUtil.toString(this);
	}
	
	/**
	 * Serialize this {@link AuthenticationFile} to a {@link JsonObject}.
	 * <p>
	 * Attention: The data is in plain text and can be read by anyone that has access to the byte array data (e.g. writing
	 * to a file). Even though this data does not contain any credentials, it contains tokens for refreshing your minecraft
	 * session that should be kept private!
	 * </p>
	 *
	 * @return Json object containing the {@link AuthenticationFile} data
	 */
	public JsonObject serialize() {
		return AuthenticationFileUtil.toJson(this);
	}
	
}
