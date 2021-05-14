package net.hycrafthd.minecraft_authenticator.microsoft;

import java.net.URL;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil;
import net.hycrafthd.minecraft_authenticator.util.HttpResponse;
import net.hycrafthd.minecraft_authenticator.util.Parameters;

public class MicrosoftService {
	
	public static void authorizationCodeToToken() {
		Parameters parameters = Parameters.create() //
				.add("client_id", Constants.MICROSOFT_CLIENT_ID) //
				.add("code", "MYSDOFDOFOS") //
				.add("grant_type", "authorization_code") //
				.add("redirect_uri", Constants.MICROSOFT_REDIRECT_URL);
		
		try {
			final URL url = ConnectionUtil.urlBuilder(Constants.MICROSOFT_OAUTH_TOKEN_SERVICE);
			
			System.out.println(url);
			
			final HttpResponse response = ConnectionUtil.urlEncodedRequest(url, parameters);
			
			System.out.println(response.getResponseCode());
			System.out.println(response.getAsString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
