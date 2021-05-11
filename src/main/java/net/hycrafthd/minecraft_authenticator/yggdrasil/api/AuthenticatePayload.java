package net.hycrafthd.minecraft_authenticator.yggdrasil.api;

public class AuthenticatePayload {
	
	private final Agent agent;
	private final String username;
	private final String password;
	private final String clientToken;
	private final boolean requestUser;
	
	public AuthenticatePayload(Agent agent, String username, String password, String clientToken, boolean requestUser) {
		this.agent = agent;
		this.username = username;
		this.password = password;
		this.clientToken = clientToken;
		this.requestUser = requestUser;
	}
	
	public Agent getAgent() {
		return agent;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getClientToken() {
		return clientToken;
	}
	
	public boolean isRequestUser() {
		return requestUser;
	}
	
	@Override
	public String toString() {
		return "AuthenticatePayload [agent=" + agent + ", username=" + username + ", password=" + password + ", clientToken=" + clientToken + ", requestUser=" + requestUser + "]";
	}
	
	public static class Agent {
		
		private final String name;
		private final int version;
		
		public Agent(String name, int version) {
			this.name = name;
			this.version = version;
		}
		
		public String getName() {
			return name;
		}
		
		public int getVersion() {
			return version;
		}
		
		@Override
		public String toString() {
			return "Agent [name=" + name + ", version=" + version + "]";
		}
	}
}
