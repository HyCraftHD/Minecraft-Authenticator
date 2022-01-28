package net.hycrafthd.minecraft_authenticator.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import net.hycrafthd.minecraft_authenticator.login.User;

public class CustomAzureApplicationMain {
	
	private static final String AZURE_CLIENT_ID = "78590d64-3549-4c5f-9ef5-add1e816fed1";
	private static final String AZURE_REDIRECT_PATH = "/ms-oauth/response";
	private static final String AZURE_REDIRECT_URL = "http://localhost:{port}" + AZURE_REDIRECT_PATH;
	
	public static void main(String[] args) throws Exception {
		try (ServerSocket server = new ServerSocket(0)) {
			final int port = server.getLocalPort();
			final String redirectUrl = AZURE_REDIRECT_URL.replace("{port}", Integer.toString(port));
			
			System.out.println(Authenticator.microsoftLogin(AZURE_CLIENT_ID, redirectUrl));
			
			// Desktop.getDesktop().browse(Authenticator.microsoftLogin(AZURE_CLIENT_ID, redirectUrl).toURI());
			
			final Socket socket = server.accept();
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			final String getRequest = bufferedReader.readLine().split(" ")[1];
			
			final String authCode = getRequest.substring((AZURE_REDIRECT_PATH + "?code=").length());
			
			System.out.println(authCode);
			
			final Authenticator authenticator = Authenticator.ofMicrosoft(authCode).customAzureApplication(AZURE_CLIENT_ID, redirectUrl).shouldAuthenticate().run();
			final User user = authenticator.getUser().get();
			System.out.println(user);
		}
	}
	
}
