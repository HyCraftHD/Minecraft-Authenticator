package net.hycrafthd.minecraft_authenticator.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ParseUtil {
	
	public static String decodeJWT(String token) {
		final String[] splitAccessToken = token.split("\\.");
		
		return decodeBase64(splitAccessToken[1]);
	}
	
	public static String encodeBase64(String string) {
		return new String(Base64.getUrlEncoder().encode(string.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}
	
	public static String decodeBase64(String string) {
		return new String(Base64.getUrlDecoder().decode(string.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}
	
}
