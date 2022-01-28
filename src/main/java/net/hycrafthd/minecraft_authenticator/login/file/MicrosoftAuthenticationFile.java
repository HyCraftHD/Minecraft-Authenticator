package net.hycrafthd.minecraft_authenticator.login.file;

public final class MicrosoftAuthenticationFile extends AuthenticationFile {
	
	private final String refreshToken;
	
	public MicrosoftAuthenticationFile(String refreshToken) {
		super(Type.MICROSOFT);
		this.refreshToken = refreshToken;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((refreshToken == null) ? 0 : refreshToken.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null)
			return false;
		if (getClass() != object.getClass())
			return false;
		final MicrosoftAuthenticationFile other = (MicrosoftAuthenticationFile) object;
		if (refreshToken == null) {
			if (other.refreshToken != null)
				return false;
		} else if (!refreshToken.equals(other.refreshToken))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "MicrosoftAuthenticationFile [refreshToken=" + refreshToken + ", toString()=" + super.toString() + "]";
	}
	
}