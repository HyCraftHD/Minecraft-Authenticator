package net.hycrafthd.minecraft_authenticator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;
import net.hycrafthd.minecraft_authenticator.util.AuthenticationUtil;

public class Main {
	
	public static void main(String[] args) throws IOException {
		System.out.println("This setup creates a auth file for further authentication without user input");
		
		try (final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.println("Type the file path for the auth-file");
			final String path = reader.readLine();
			final Path authFile = Paths.get(path);
			do {
				System.out.println("Type 'microsoft' or 'mojang' for account type");
				final String type = reader.readLine();
				if (type.equals("microsoft")) {
					System.out.println("Open the following link and log into your microsoft account");
					System.out.println(MicrosoftService.oAuthLoginUrl());
					System.out.println("Paste the code parameter of the returned url");
					final String authCode = reader.readLine();
					try {
						final Authenticator authenticator = Authenticator.ofMicrosoft(authCode).run();
						AuthenticationUtil.writeAuthenticationFile(authenticator.getResultFile(), authFile);
					} catch (IOException | AuthenticationException ex) {
						throw new IllegalStateException("An error occured while trying to create auth file", ex);
					}
					System.out.println("Successfully created microsoft auth file");
					return;
				} else if (type.equals("mojang")) {
					System.out.println("Type in your username / email");
					final String username = reader.readLine();
					System.out.println("Type in your password");
					final String password = reader.readLine();
					System.out.println("Type in your client token (This will be used for every request and keeps you logged in)");
					final String clientToken = reader.readLine();
					
					try {
						final Authenticator authenticator = Authenticator.ofYggdrasil(clientToken, username, password).run();
						AuthenticationUtil.writeAuthenticationFile(authenticator.getResultFile(), authFile);
					} catch (IOException | AuthenticationException ex) {
						throw new IllegalStateException("An error occured while trying to create auth file", ex);
					}
					System.out.println("Successfully created mojang auth file");
					return;
				}
			} while (true);
		}
	}
}
