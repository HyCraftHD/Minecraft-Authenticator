package net.hycrafthd.minecraft_authenticator.microsoft.api;

import java.util.List;

public class MinecraftProfileResponse {
	
	private final String id;
	private final String name;
	private final List<Skin> skins;
	private final List<Cape> capes;
	
	public MinecraftProfileResponse(String id, String name, List<Skin> skins, List<Cape> capes) {
		this.id = id;
		this.name = name;
		this.skins = skins;
		this.capes = capes;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Skin> getSkins() {
		return skins;
	}
	
	public List<Cape> getCapes() {
		return capes;
	}
	
	@Override
	public String toString() {
		return "MinecraftProfileResponse [id=" + id + ", name=" + name + ", skins=" + skins + ", capes=" + capes + "]";
	}
	
	public static class Skin {
		
		private final String id;
		private final String state;
		private final String url;
		private final String variant;
		private final String alias;
		
		public Skin(String id, String state, String url, String variant, String alias) {
			this.id = id;
			this.state = state;
			this.url = url;
			this.variant = variant;
			this.alias = alias;
		}
		
		public String getId() {
			return id;
		}
		
		public String getState() {
			return state;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getVariant() {
			return variant;
		}
		
		public String getAlias() {
			return alias;
		}
		
		@Override
		public String toString() {
			return "Skin [id=" + id + ", state=" + state + ", url=" + url + ", variant=" + variant + ", alias=" + alias + "]";
		}
		
	}
	
	public static class Cape {
		
		private final String id;
		private final String state;
		private final String url;
		private final String alias;
		
		public Cape(String id, String state, String url, String alias) {
			this.id = id;
			this.state = state;
			this.url = url;
			this.alias = alias;
		}
		
		public String getId() {
			return id;
		}
		
		public String getState() {
			return state;
		}
		
		public String getUrl() {
			return url;
		}
		
		public String getAlias() {
			return alias;
		}
		
		@Override
		public String toString() {
			return "Cape [id=" + id + ", state=" + state + ", url=" + url + ", alias=" + alias + "]";
		}
	}
}
