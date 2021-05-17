package net.hycrafthd.minecraft_authenticator.microsoft.api;

public class MinecraftLoginWithXBoxPayload {
	
	private final String identityToken;
	
	public MinecraftLoginWithXBoxPayload(String identityToken) {
		this.identityToken = identityToken;
	}
	
	public String getIdentityToken() {
		return identityToken;
	}
	
	@Override
	public String toString() {
		return "MinecraftLoginWithXBoxPayload [identityToken=" + identityToken + "]";
	}
	
}
