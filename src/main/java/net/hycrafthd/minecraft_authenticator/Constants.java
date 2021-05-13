package net.hycrafthd.minecraft_authenticator;

public class Constants {
	
	public static final String USER_AGENT = "Minecraft-Authenticator";
	
	// Mojang Authentication
	public static final String YGGDRASIL_SERVICE = "https://authserver.mojang.com";
	public static final String YGGDRASIL_ENDPOINT_AUTHENTICATE = "authenticate";
	public static final String YGGDRASIL_ENDPOINT_REFRESH = "refresh";
	public static final String YGGDRASIL_ENDPOINT_VALIDATE = "validate";
	
	// Microsoft Authentication
	public static final String MS_CLIENT_ID = "00000000402b5328";
	public static final String MS_REDIRECT_URL = "https://login.live.com/oauth20_desktop.srf";
	
	public static final String MS_OAUTH_TOKEN_SERVICE = "https://login.live.com/oauth20_token.srf";
	
}
