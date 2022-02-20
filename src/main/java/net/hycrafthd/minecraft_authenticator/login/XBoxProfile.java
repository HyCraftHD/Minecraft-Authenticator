package net.hycrafthd.minecraft_authenticator.login;

import java.util.List;

public record XBoxProfile(String xuid, List<XBoxProfileSettings> settings, boolean isSponsoredUser) {
	
	public static record XBoxProfileSettings(String id, String value) {
	}
	
}
