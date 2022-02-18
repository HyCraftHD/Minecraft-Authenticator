package net.hycrafthd.minecraft_authenticator.login.file;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;

public class MicrosoftAuthenticationFile extends AuthenticationFile {
	
	private final String refreshToken;
	
	public MicrosoftAuthenticationFile(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	@Override
	public String getRefreshToken() {
		return refreshToken;
	}
	
	@Override
	public String toString() {
		return "MicrosoftAuthenticationFile [refreshToken=" + refreshToken + "]";
	}
	
}
