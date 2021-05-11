package net.hycrafthd.minecraft_authenticator.yggdrasil.api;

public class RefreshPayload {
	
	private final String accessToken;
	private final String clientToken;
	private final Profile selectedProfile;
	private final boolean requestUser;
	
	public RefreshPayload(String accessToken, String clientToken, Profile selectedProfile, boolean requestUser) {
		this.accessToken = accessToken;
		this.clientToken = clientToken;
		this.selectedProfile = selectedProfile;
		this.requestUser = requestUser;
	}
	
	public String getAccessToken() {
		return accessToken;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
	public Profile getSelectedProfile() {
		return selectedProfile;
	}
	
	public boolean isRequestUser() {
		return requestUser;
	}
	
	@Override
	public String toString() {
		return "RefreshPayload [accessToken=" + accessToken + ", clientToken=" + clientToken + ", selectedProfile=" + selectedProfile + ", requestUser=" + requestUser + "]";
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
