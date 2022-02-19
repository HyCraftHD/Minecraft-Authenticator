package net.hycrafthd.minecraft_authenticator.microsoft.api;

public class MinecraftLauncherLoginPayload {
	
	private final String xtoken;
	private final String platform;
	
	public MinecraftLauncherLoginPayload(String xtoken, String platform) {
		this.xtoken = xtoken;
		this.platform = platform;
	}
	
	public String getXtoken() {
		return xtoken;
	}
	
	public String getPlatform() {
		return platform;
	}
	
	@Override
	public String toString() {
		return "MinecraftLauncherLoginPayload [xtoken=" + xtoken + ", platform=" + platform + "]";
	}
	
}
