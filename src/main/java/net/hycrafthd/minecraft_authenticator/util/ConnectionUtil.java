package net.hycrafthd.minecraft_authenticator.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ConnectionUtil {
	
	public static HttpResponse jsonRequest(URL url, HttpPayload payload) throws IOException {
		return postRequest(url, "application/json", payload);
	}
	
	public static HttpResponse urlEncodedRequest(URL url) throws IOException {
		return postRequest(url, "application/x-www-form-urlencoded", HttpPayload.EMPTY);
	}
	
	public static HttpResponse postRequest(URL url, String contentType, HttpPayload payload) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(15000);
		urlConnection.setReadTimeout(15000);
		urlConnection.setUseCaches(false);
		urlConnection.setInstanceFollowRedirects(true);
		urlConnection.setDoInput(true);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Charset", "UTF-8");
		urlConnection.addRequestProperty("User-Agent", "Minecraft-Authenticator");
		urlConnection.setRequestProperty("Content-Type", contentType);
		
		if (payload.hasContent()) {
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("Content-Length", Integer.toString(payload.getSize()));
		}
		
		urlConnection.connect();
		
		if (payload.hasContent()) {
			try (final OutputStream outputStream = urlConnection.getOutputStream()) {
				payload.write(outputStream);
			}
		}
		
		try (final InputStream inputStream = urlConnection.getInputStream()) {
			return HttpResponse.fromStream(urlConnection.getResponseCode(), inputStream);
		} catch (IOException ex) {
			try (final InputStream inputStream = urlConnection.getErrorStream()) {
				return HttpResponse.fromStream(urlConnection.getResponseCode(), inputStream);
			}
		}
	}
	
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
