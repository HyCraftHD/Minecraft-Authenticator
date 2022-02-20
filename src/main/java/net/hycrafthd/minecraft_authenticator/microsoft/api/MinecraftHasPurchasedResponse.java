package net.hycrafthd.minecraft_authenticator.microsoft.api;

import java.util.List;

public class MinecraftHasPurchasedResponse {
	
	private final List<Item> items;
	private final String signature;
	private final String keyId;
	private final String requestId;
	
	public MinecraftHasPurchasedResponse(List<Item> items, String signature, String keyId, String requestId) {
		this.items = items;
		this.signature = signature;
		this.keyId = keyId;
		this.requestId = requestId;
	}
	
	public List<Item> getItems() {
		return items;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public String getKeyId() {
		return keyId;
	}
	
	public String getRequestId() {
		return requestId;
	}
	
	@Override
	public String toString() {
		return "MinecraftHasPurchasedResponse [items=" + items + ", signature=" + signature + ", keyId=" + keyId + ", requestId=" + requestId + "]";
	}
	
	public static class Item {
		
		private final String name;
		private final String source;
		
		public Item(String name, String source) {
			this.name = name;
			this.source = source;
		}
		
		public String getName() {
			return name;
		}
		
		public String getSource() {
			return source;
		}
		
		@Override
		public String toString() {
			return "Item [name=" + name + ", source=" + source + "]";
		}
		
	}
}
