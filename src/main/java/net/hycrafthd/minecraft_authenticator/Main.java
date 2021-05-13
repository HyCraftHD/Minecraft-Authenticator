package net.hycrafthd.minecraft_authenticator;

import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftService;

public class Main {
	
	public static void main(String[] args) throws Exception {
		MicrosoftService.authorizationCodeToToken();
	}
	
}
