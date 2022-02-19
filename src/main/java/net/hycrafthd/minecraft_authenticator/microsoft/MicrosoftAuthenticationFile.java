package net.hycrafthd.minecraft_authenticator.microsoft;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;

public class MicrosoftAuthenticationFile extends AuthenticationFile {
	
	private final String refreshToken;
	
	public MicrosoftAuthenticationFile(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	/**
	 * The refresh token for microsoft oAuth
	 *
	 * @return Refresh token
	 */
	public String getRefreshToken() {
		return refreshToken;
	}
	
	@Override
	public String toString() {
		return "MicrosoftAuthenticationFile [refreshToken=" + refreshToken + "]";
	}
	
}
