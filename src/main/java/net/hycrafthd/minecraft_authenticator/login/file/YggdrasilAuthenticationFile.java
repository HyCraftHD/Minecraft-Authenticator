package net.hycrafthd.minecraft_authenticator.login.file;

public final class YggdrasilAuthenticationFile extends AuthenticationFile {
	
	private final String accessToken;
	private final String clientToken;
	
	public YggdrasilAuthenticationFile(String accessToken, String clientToken) {
		super(Type.YGGDRASIL);
		this.accessToken = accessToken;
		this.clientToken = clientToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessToken == null) ? 0 : accessToken.hashCode());
		result = prime * result + ((clientToken == null) ? 0 : clientToken.hashCode());
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
		final YggdrasilAuthenticationFile other = (YggdrasilAuthenticationFile) object;
		if (accessToken == null) {
			if (other.accessToken != null)
				return false;
		} else if (!accessToken.equals(other.accessToken))
			return false;
		if (clientToken == null) {
			if (other.clientToken != null)
				return false;
		} else if (!clientToken.equals(other.clientToken))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "YggdrasilAuthenticationFile [accessToken=" + accessToken + ", clientToken=" + clientToken + ", toString()=" + super.toString() + "]";
	}
	
}