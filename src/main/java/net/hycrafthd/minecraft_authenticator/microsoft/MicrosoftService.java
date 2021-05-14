package net.hycrafthd.minecraft_authenticator.microsoft;

import java.net.MalformedURLException;
import java.net.URL;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil;
import net.hycrafthd.minecraft_authenticator.util.HttpResponse;
import net.hycrafthd.minecraft_authenticator.util.Parameters;

public class MicrosoftService {
	
	public static URL generateOAuthLoginUrl() {
		final Parameters parameters = Parameters.create() //
				.add("client_id", Constants.MICROSOFT_CLIENT_ID) //
				.add("response_type", "code") //
				.add("scope", "XboxLive.signin offline_access") //
				.add("redirect_uri", Constants.MICROSOFT_OAUTH_REDIRECT_URL);
		
		try {
			return ConnectionUtil.urlBuilder(Constants.MICROSOFT_OAUTH_SERVICE, Constants.MICROSOFT_OAUTH_ENDPOINT_AUTHORIZE, parameters);
		} catch (MalformedURLException ex) {
			return null;
		}
	}
	
	public static void requestOAuthAuthorizationToken(String authorizationCode) {
		final Parameters parameters = Parameters.create() //
				.add("client_id", Constants.MICROSOFT_CLIENT_ID) //
				.add("code", authorizationCode) //
				.add("grant_type", "authorization_code") //
				.add("redirect_uri", Constants.MICROSOFT_OAUTH_REDIRECT_URL);
		
		try {
			final URL url = ConnectionUtil.urlBuilder(Constants.MICROSOFT_OAUTH_SERVICE, Constants.MICROSOFT_OAUTH_ENDPOINT_TOKEN);
			
			System.out.println(url);
			
			final HttpResponse response = ConnectionUtil.urlEncodedRequest(url, parameters);
			
			System.out.println(response.getResponseCode());
			System.out.println(response.getAsString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
