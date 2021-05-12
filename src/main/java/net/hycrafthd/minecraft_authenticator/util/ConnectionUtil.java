package net.hycrafthd.minecraft_authenticator.util;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ConnectionUtil {
	
	public static URL urlBuilder(String baseUrl, String path, Map<String, Object> parameters) throws MalformedURLException {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(baseUrl);
		builder.append("/");
		builder.append(path);
		
		if (!parameters.isEmpty()) {
			builder.append("?");
		}
		
		boolean needAnd = false;
		
		for (final Map.Entry<String, Object> entry : parameters.entrySet()) {
			if (needAnd) {
				builder.append("&");
			}
			needAnd = true;
			
			builder.append(urlEncode(entry.getKey()));
			builder.append("=");
			builder.append(urlEncode(entry.getValue()));
			
		}
		return new URL(builder.toString());
	}
	
	private static String urlEncode(Object object) {
		try {
			return URLEncoder.encode(object.toString(), StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException ex) {
			throw new IllegalStateException("UTF_8 encoding is not present");
		}
	}
	
}
