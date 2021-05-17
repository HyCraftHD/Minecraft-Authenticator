package net.hycrafthd.minecraft_authenticator.login;

public class User {
	
	private final String uuid;
	private final String name;
	private final String accessToken;
	private final String type;
	
	public User(String uuid, String name, String accessToken, String type) {
		this.uuid = uuid;
		this.name = name;
		this.accessToken = accessToken;
		this.type = type;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "User [uuid=" + uuid + ", name=" + name + ", accessToken=" + accessToken + ", type=" + type + "]";
	}
	
}
