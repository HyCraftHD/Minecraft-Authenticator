package net.hycrafthd.minecraft_authenticator.yggdrasil.api;

import java.util.List;

public class RefreshResponse {
	
	private final String clientToken;
	private final String accessToken;
	private final Profile selectedProfile;
	private final User user;
	
	public RefreshResponse(String clientToken, String accessToken, Profile selectedProfile, User user) {
		this.clientToken = clientToken;
		this.accessToken = accessToken;
		this.selectedProfile = selectedProfile;
		this.user = user;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public Profile getSelectedProfile() {
		return selectedProfile;
	}
	
	public User getUser() {
		return user;
	}
	
	@Override
	public String toString() {
		return "RefreshResponse [clientToken=" + clientToken + ", accessToken=" + accessToken + ", selectedProfile=" + selectedProfile + ", user=" + user + "]";
	}
	
	public static class Profile {
		
		private final String name;
		private final String id;
		
		public Profile(String name, String id) {
			this.name = name;
			this.id = id;
		}
		
		public String getName() {
			return name;
		}
		
		public String getId() {
			return id;
		}
		
		@Override
		public String toString() {
			return "Profile [name=" + name + ", id=" + id + "]";
		}
	}
	
	public static class User {
		
		private final String id;
		private final List<Properties> properties;
		
		public User(List<Properties> properties, String id) {
			this.properties = properties;
			this.id = id;
		}
		
		public List<Properties> getProperties() {
			return properties;
		}
		
		public String getId() {
			return id;
		}
		
		@Override
		public String toString() {
			return "User [id=" + id + ", properties=" + properties + "]";
		}
		
		public static class Properties {
			
			private final String name;
			private final String value;
			
			public Properties(String name, String value) {
				this.name = name;
				this.value = value;
			}
			
			public String getName() {
				return name;
			}
			
			public String getValue() {
				return value;
			}
			
			@Override
			public String toString() {
				return "Properties [name=" + name + ", value=" + value + "]";
			}
		}
	}
}
