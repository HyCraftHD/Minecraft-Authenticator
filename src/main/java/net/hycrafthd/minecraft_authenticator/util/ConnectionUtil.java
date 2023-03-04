package net.hycrafthd.minecraft_authenticator.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.util.function.ConsumerWithIOException;
import net.hycrafthd.minecraft_authenticator.util.function.FunctionWithIOException;

public class ConnectionUtil {
	
	public static final String JSON_CONTENT_TYPE = "application/json";
	public static final String URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
	
	private static final ConsumerWithIOException<HttpURLConnection> NO_OP = urlConnection -> {
	};
	
	public static HttpResponse jsonPostRequest(URL url, HttpPayload payload, TimeoutValues timeoutValues) throws IOException {
		return postRequest(url, JSON_CONTENT_TYPE, JSON_CONTENT_TYPE, payload, NO_OP, timeoutValues);
	}
	
	public static HttpResponse urlEncodedPostRequest(URL url, String acceptType, Map<String, Object> parameters, TimeoutValues timeoutValues) throws IOException {
		return postRequest(url, acceptType, URL_ENCODED_CONTENT_TYPE, HttpPayload.fromString(createUrlEncodedParameters(parameters, false)), NO_OP, timeoutValues);
	}
	
	public static HttpResponse bearerAuthorizationJsonGetRequest(URL url, String token, TimeoutValues timeoutValues) throws IOException {
		return authorizationJsonGetRequest(url, "Bearer " + token, NO_OP, timeoutValues);
	}
	
	public static HttpResponse authorizationJsonGetRequest(URL url, String authorization, ConsumerWithIOException<HttpURLConnection> preConnect, TimeoutValues timeoutValues) throws IOException {
		return getRequest(url, JSON_CONTENT_TYPE, urlConnection -> {
			urlConnection.setRequestProperty("Authorization", authorization);
			preConnect.accept(urlConnection);
		}, timeoutValues);
	}
	
	public static HttpResponse postRequest(URL url, String acceptType, String contentType, HttpPayload payload, ConsumerWithIOException<HttpURLConnection> preConnect, TimeoutValues timeoutValues) throws IOException {
		return basicRequest(url, "POST", acceptType, urlConnection -> {
			urlConnection.setDoOutput(true);
			urlConnection.setRequestProperty("Charset", StandardCharsets.UTF_8.name());
			urlConnection.setRequestProperty("Content-Type", contentType);
			urlConnection.setFixedLengthStreamingMode(payload.getSize());
			preConnect.accept(urlConnection);
		}, urlConnection -> {
			if (payload.hasContent()) {
				try (final OutputStream outputStream = urlConnection.getOutputStream()) {
					payload.write(outputStream);
				}
			}
		}, timeoutValues);
	}
	
	public static HttpResponse getRequest(URL url, String acceptType, ConsumerWithIOException<HttpURLConnection> preConnect, TimeoutValues timeoutValues) throws IOException {
		return basicRequest(url, "GET", acceptType, preConnect, NO_OP, timeoutValues);
	}
	
	public static HttpResponse basicRequest(URL url, String method, String acceptType, ConsumerWithIOException<HttpURLConnection> preConnect, ConsumerWithIOException<HttpURLConnection> postConnect, TimeoutValues timeoutValues) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(timeoutValues.connectTimeout());
		urlConnection.setReadTimeout(timeoutValues.readTimeout());
		urlConnection.setUseCaches(false);
		urlConnection.setInstanceFollowRedirects(true);
		urlConnection.setDoInput(true);
		urlConnection.setRequestMethod(method);
		urlConnection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
		urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		urlConnection.setRequestProperty("Accept", acceptType);
		urlConnection.setRequestProperty("User-Agent", Constants.USER_AGENT);
		
		preConnect.accept(urlConnection);
		
		urlConnection.connect();
		
		postConnect.accept(urlConnection);
		
		try (final InputStream inputStream = getInputStream(urlConnection, HttpURLConnection::getInputStream)) {
			return HttpResponse.fromStream(urlConnection.getResponseCode(), inputStream);
		} catch (final IOException ex) {
			try (final InputStream inputStream = getInputStream(urlConnection, HttpURLConnection::getErrorStream)) {
				return HttpResponse.fromStream(urlConnection.getResponseCode(), inputStream);
			}
		}
	}
	
	private static InputStream getInputStream(HttpURLConnection urlConnection, FunctionWithIOException<HttpURLConnection, InputStream> function) throws IOException {
		final String encoding = urlConnection.getContentEncoding();
		
		final InputStream inputStream = function.apply(urlConnection);
		
		if ("gzip".equalsIgnoreCase(encoding)) {
			return new GZIPInputStream(inputStream);
		} else if ("deflate".equalsIgnoreCase(encoding)) {
			return new InflaterInputStream(inputStream, new Inflater(true));
		} else {
			return inputStream;
		}
	}
	
	public static URL urlBuilder(String url) throws MalformedURLException {
		return urlBuilder(url, (String) null);
	}
	
	public static URL urlBuilder(String baseUrl, String path) throws MalformedURLException {
		return urlBuilder(baseUrl, path, Collections.emptyMap());
	}
	
	public static URL urlBuilder(String url, Map<String, Object> parameters) throws MalformedURLException {
		return urlBuilder(url, null, parameters);
	}
	
	public static URL urlBuilder(String baseUrl, String path, Map<String, Object> parameters) throws MalformedURLException {
		final StringBuilder builder = new StringBuilder();
		
		builder.append(baseUrl);
		if (path != null && !path.isEmpty()) {
			builder.append("/");
			builder.append(path);
		}
		
		if (!parameters.isEmpty()) {
			builder.append("?");
		}
		
		appendUrlEncodedParameters(builder, parameters, true);
		
		return new URL(builder.toString());
	}
	
	private static String createUrlEncodedParameters(Map<String, Object> parameters, boolean percent20) {
		final StringBuilder builder = new StringBuilder();
		appendUrlEncodedParameters(builder, parameters, percent20);
		return builder.toString();
	}
	
	private static StringBuilder appendUrlEncodedParameters(StringBuilder builder, Map<String, Object> parameters, boolean percent20) {
		boolean needAnd = false;
		for (final Map.Entry<String, Object> entry : parameters.entrySet()) {
			if (needAnd) {
				builder.append("&");
			}
			needAnd = true;
			builder.append(escape(entry.getKey(), percent20));
			builder.append("=");
			builder.append(escape(entry.getValue(), percent20));
		}
		return builder;
	}
	
	private static String escape(Object object, boolean percent20) {
		final String encoded = URLEncoder.encode(object.toString(), StandardCharsets.UTF_8);
		if (percent20) {
			return encoded.replace("+", "%20");
		} else {
			return encoded;
		}
	}
	
	public static record TimeoutValues(int connectTimeout, int readTimeout) {
	}
	
}
