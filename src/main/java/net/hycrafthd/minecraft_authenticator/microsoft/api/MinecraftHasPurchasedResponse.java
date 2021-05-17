package net.hycrafthd.minecraft_authenticator.microsoft.api;

import java.util.List;

public class MinecraftHasPurchasedResponse {
	
	private final List<Item> items;
	private final String signature;
	private final String keyId;
	
	public MinecraftHasPurchasedResponse(List<Item> items, String signature, String keyId) {
		this.items = items;
		this.signature = signature;
		this.keyId = keyId;
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
	
	@Override
	public String toString() {
		return "MinecraftHasPurchasedResponse [items=" + items + ", signature=" + signature + ", keyId=" + keyId + "]";
	}
	
	public static class Item {
		
		private final String name;
		private final String signature;
		
		public Item(String name, String signature) {
			this.name = name;
			this.signature = signature;
		}
		
		public String getName() {
			return name;
		}
		
		public String getSignature() {
			return signature;
		}
		
		@Override
		public String toString() {
			return "Item [name=" + name + ", signature=" + signature + "]";
		}
	}
}
