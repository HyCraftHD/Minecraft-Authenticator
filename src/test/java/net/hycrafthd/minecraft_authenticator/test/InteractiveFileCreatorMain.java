package net.hycrafthd.minecraft_authenticator.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.Authenticator;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;

public class InteractiveFileCreatorMain {
	
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
					try (OutputStream outputStream = Files.newOutputStream(authFile, StandardOpenOption.CREATE)) {
						final Authenticator authenticator = Authenticator.ofMicrosoft(authCode).build();
						authenticator.run();
						authenticator.getResultFile().writeCompressed(outputStream);
					} catch (IOException | AuthenticationException ex) {
						throw new IllegalStateException("An error occured while trying to create auth file", ex);
					}
					System.out.println("Successfully created microsoft auth file");
					return;
				}
			} while (true);
		}
	}
}
