package net.hycrafthd.minecraft_authenticator.microsoft;

import java.util.Objects;
import java.util.UUID;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationFile;

public class MicrosoftAuthenticationFile extends AuthenticationFile {
	
	private final String refreshToken;
	
	public MicrosoftAuthenticationFile(UUID clientId, String refreshToken) {
		super(clientId);
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(refreshToken);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj) || (getClass() != obj.getClass()))
			return false;
		MicrosoftAuthenticationFile other = (MicrosoftAuthenticationFile) obj;
		return Objects.equals(refreshToken, other.refreshToken);
	}
	
	@Override
	public String toString() {
		return "MicrosoftAuthenticationFile [refreshToken=" + refreshToken + ", toString()=" + super.toString() + "]";
	}
	
}
