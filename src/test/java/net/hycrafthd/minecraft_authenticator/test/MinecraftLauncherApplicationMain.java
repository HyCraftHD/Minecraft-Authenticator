package net.hycrafthd.minecraft_authenticator.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import net.hycrafthd.minecraft_authenticator.login.User;
import net.hycrafthd.minecraft_authenticator.login.XBoxProfile;

public class MinecraftLauncherApplicationMain {
	
	public static void main(String[] args) throws Exception {
		System.out.println(Authenticator.microsoftLogin());
		
		final String authCode;
		
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.println("Type in the auth code");
			authCode = reader.readLine();
		}
		
		System.out.println("Auth code: " + authCode);
		
		final Authenticator authenticator = Authenticator.ofMicrosoft(authCode) //
				.serviceConnectTimeout(5000) //
				.serviceReadTimeout(10000) //
				.shouldAuthenticate() //
				.shouldRetrieveXBoxProfile() //
				.build();
		
		try {
			authenticator.run();
		} catch (final AuthenticationException ex) {
			ex.printStackTrace(System.out);
			System.out.println("Updated auth file: " + authenticator.getResultFile());
			return;
		}
		
		final User user = authenticator.getUser().get();
		System.out.println(user);
		
		final XBoxProfile xBoxProfile = authenticator.getXBoxProfile().get();
		System.out.println(xBoxProfile);
	}
}
