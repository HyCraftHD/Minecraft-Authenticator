package net.hycrafthd.minecraft_authenticator.login;

/**
 * This class is the result of the authentication and contains the minecraft access token with profile data
 */
public class User {
	
	private final String uuid;
	private final String name;
	private final String accessToken;
	private final String type;
	
	/**
	 * Creates a new minecraft user
	 *
	 * @param uuid Player uuid
	 * @param name Player name
	 * @param accessToken Minecraft access token
	 * @param type Account type. Should be mojang or msa
	 */
	public User(String uuid, String name, String accessToken, String type) {
		this.uuid = uuid;
		this.name = name;
		this.accessToken = accessToken;
		this.type = type;
	}
	
	/**
	 * Players uuid
	 *
	 * @return uuid
	 */
	public String getUuid() {
		return uuid;
	}
	
	/**
	 * Players name
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Accounts access token
	 *
	 * @return access token
	 */
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * Account type (mojang or msa)
	 *
	 * @return account type
	 */
	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "User [uuid=" + uuid + ", name=" + name + ", accessToken=" + accessToken + ", type=" + type + "]";
	}
	
}
