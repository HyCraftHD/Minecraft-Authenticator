package net.hycrafthd.minecraft_authenticator.microsoft.api;

import java.util.List;

public class XBoxProfileResponse {
	
	private final List<Profile> profileUsers;
	
	public XBoxProfileResponse(List<Profile> profileUsers) {
		this.profileUsers = profileUsers;
	}
	
	public List<Profile> getProfileUsers() {
		return profileUsers;
	}
	
	@Override
	public String toString() {
		return "XBoxProfileResponse [profileUsers=" + profileUsers + "]";
	}
	
	public static class Profile {
		
		private final String id;
		private final String hostId;
		private final List<Settings> settings;
		private final boolean isSponsoredUser;
		
		public Profile(String id, String hostId, List<Settings> settings, boolean isSponsoredUser) {
			this.id = id;
			this.hostId = hostId;
			this.settings = settings;
			this.isSponsoredUser = isSponsoredUser;
		}
		
		public String getId() {
			return id;
		}
		
		public String getHostId() {
			return hostId;
		}
		
		public List<Settings> getSettings() {
			return settings;
		}
		
		public boolean isSponsoredUser() {
			return isSponsoredUser;
		}
		
		@Override
		public String toString() {
			return "Profile [id=" + id + ", hostId=" + hostId + ", settings=" + settings + ", isSponsoredUser=" + isSponsoredUser + "]";
		}
		
		public static class Settings {
			
			private final String id;
			private final String value;
			
			public Settings(String id, String value) {
				this.id = id;
				this.value = value;
			}
			
			public String getId() {
				return id;
			}
			
			public String getValue() {
				return value;
			}
			
			@Override
			public String toString() {
				return "Settings [id=" + id + ", value=" + value + "]";
			}
		}
	}
}
