package net.hycrafthd.minecraft_authenticator.yggdrasil.api;

import java.util.List;

public class AuthenticateResponse {
	
	private final User user;
	private final String clientToken;
	private final String accessToken;
	private final List<Profile> availableProfiles;
	private final Profile selectedProfile;
	
	public AuthenticateResponse(User user, String clientToken, String accessToken, List<Profile> availableProfiles, Profile selectedProfile) {
		this.user = user;
		this.clientToken = clientToken;
		this.accessToken = accessToken;
		this.availableProfiles = availableProfiles;
		this.selectedProfile = selectedProfile;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public List<Profile> getAvailableProfiles() {
		return availableProfiles;
	}
	
	public Profile getSelectedProfile() {
		return selectedProfile;
	}
	
	@Override
	public String toString() {
		return "AuthenticateResponse [user=" + user + ", clientToken=" + clientToken + ", accessToken=" + accessToken + ", availableProfiles=" + availableProfiles + ", selectedProfile=" + selectedProfile + "]";
	}
	
	public static class User {
		
		private final String username;
		private final List<Properties> properties;
		private final String id;
		
		public User(String username, List<Properties> properties, String id) {
			this.username = username;
			this.properties = properties;
			this.id = id;
		}
		
		public String getUsername() {
			return username;
		}
		
		public List<Properties> getProperties() {
			return properties;
		}
		
		public String getId() {
			return id;
		}
		
		@Override
		public String toString() {
			return "User [username=" + username + ", properties=" + properties + ", id=" + id + "]";
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
}
