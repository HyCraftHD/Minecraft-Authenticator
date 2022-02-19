package net.hycrafthd.minecraft_authenticator.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ParseUtil {
	
	public static String decodeJWT(String token) {
		final String[] splitAccessToken = token.split("\\.");
		
		return new String(Base64.getUrlDecoder().decode(splitAccessToken[1].getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}
	
}
