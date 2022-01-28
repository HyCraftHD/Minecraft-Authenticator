package net.hycrafthd.minecraft_authenticator.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
	
	private final int responseCode;
	private final byte[] bytes;
	
	private HttpResponse(int responseCode, byte[] bytes) {
		this.responseCode = responseCode;
		this.bytes = bytes;
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	public boolean hasContent() {
		return bytes.length > 0;
	}
	
	public int getSize() {
		return bytes.length;
	}
	
	public String getAsString() {
		return new String(bytes, StandardCharsets.UTF_8);
	}
	
	public static HttpResponse fromStream(int responseCode, InputStream inputStream) throws IOException {
		final byte[] bytes;
		if (inputStream == null) {
			bytes = new byte[0];
		} else {
			bytes = inputStream.readAllBytes();
		}
		return new HttpResponse(responseCode, bytes);
	}
	
}