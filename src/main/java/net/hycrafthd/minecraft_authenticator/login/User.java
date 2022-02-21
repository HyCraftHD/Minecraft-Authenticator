package net.hycrafthd.minecraft_authenticator.login;

/**
 * This class contains the minecraft profile data as well as the access token.
 */
public record User(String uuid, String name, String accessToken, String type, String xuid, String clientId) {
}
