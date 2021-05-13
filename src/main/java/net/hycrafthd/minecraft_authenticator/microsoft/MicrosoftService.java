package net.hycrafthd.minecraft_authenticator.microsoft;

import java.util.LinkedHashMap;
import java.util.Map;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil;
import net.hycrafthd.minecraft_authenticator.util.HttpResponse;

public class MicrosoftService {
	
	public static void authorizationCodeToToken() {
		final Map<String, Object> map = new LinkedHashMap<>();
		
		map.put("client_id", Constants.MICROSOFT_CLIENT_ID);
		map.put("code", "MYCODE");
		map.put("grant_type", "authorization_code");
		map.put("redirect_uri", Constants.MICROSOFT_REDIRECT_URL);
		
		try {
			final HttpResponse response = ConnectionUtil.urlEncodedRequest(ConnectionUtil.urlBuilder(Constants.MICROSOFT_OAUTH_TOKEN_SERVICE, map));
			
			System.out.println(response.getResponseCode());
			System.out.println(response.getAsString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
