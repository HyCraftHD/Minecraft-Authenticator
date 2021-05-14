package net.hycrafthd.minecraft_authenticator.util;

import java.util.LinkedHashMap;

public class Parameters extends LinkedHashMap<String, Object> {
	
	private static final long serialVersionUID = 1;
	
	public static Parameters create() {
		return new Parameters();
	}
	
	public Parameters add(String string, Object object) {
		put(string, object);
		return this;
	}
	
}
