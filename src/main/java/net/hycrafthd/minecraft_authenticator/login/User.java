package net.hycrafthd.minecraft_authenticator.login;

/**
 * This class is the result of the authentication and contains the minecraft access token with profile data
 */
public record User(String uuid, String name, String accessToken, String type, String xuid, String clientId) {
}
