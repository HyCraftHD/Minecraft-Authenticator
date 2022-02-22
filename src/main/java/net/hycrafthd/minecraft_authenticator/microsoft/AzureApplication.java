package net.hycrafthd.minecraft_authenticator.microsoft;

public record AzureApplication(String clientId, String redirectUrl, String clientSecret) {
	
	public AzureApplication(String clientId, String redirectUrl) {
		this(clientId, redirectUrl, null);
	}
}
