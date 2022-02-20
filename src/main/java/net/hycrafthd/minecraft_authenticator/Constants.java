package net.hycrafthd.minecraft_authenticator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {
	
	public static final String USER_AGENT = "Minecraft-Authenticator";
	
	public static final Gson GSON = new GsonBuilder().create();
	
	// Microsoft Authentication
	public static final String MICROSOFT_CLIENT_ID = "00000000402b5328";
	
	public static final String MICROSOFT_OAUTH_SERVICE = "https://login.live.com";
	public static final String MICROSOFT_OAUTH_REDIRECT_URL = MICROSOFT_OAUTH_SERVICE + "/oauth20_desktop.srf";
	public static final String MICROSOFT_OAUTH_ENDPOINT_AUTHORIZE = "oauth20_authorize.srf";
	public static final String MICROSOFT_OAUTH_ENDPOINT_TOKEN = "oauth20_token.srf";
	
	public static final String MICROSOFT_XBL_AUTHENTICATE_URL = "https://user.auth.xboxlive.com/user/authenticate";
	public static final String MICROSOFT_XSTS_AUTHORIZE_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
	
	public static final String MICROSOFT_MINECRAFT_SERVICE = "https://api.minecraftservices.com";
	public static final String MICROSOFT_MINECRAFT_ENDPOINT_LAUNCHER_LOGIN = "launcher/login";
	public static final String MICROSOFT_MINECRAFT_ENDPOINT_HAS_PURCHASED = "entitlements/license";
	public static final String MICROSOFT_MINECRAFT_ENDPOINT_PROFILE = "minecraft/profile";
	
	public static final String MICROSOFT_XBOX_PROFILE_SETTINGS_URL = "https://profile.xboxlive.com/users/me/profile/settings";
	
	// File warning
	public static final String FILE_WARNING = "DO NOT SHARE THIS FILE. IT CONTAINS LOGIN INFORMATION FOR YOUR MINECRAFT ACCOUNT!";
	
}
