package net.hycrafthd.minecraft_authenticator.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;

import net.hycrafthd.minecraft_authenticator.Constants;

public class ConnectionUtil {
	
	public static final String JSON_CONTENT_TYPE = "application/json";
	public static final String URL_ENCODED_CONTENT_TYPE = "application/x-www-form-urlencoded";
	
	private static final ConsumerWithIOException<HttpURLConnection> NO_OP = urlConnection -> {
	};
	
	public static HttpResponse jsonPostRequest(URL url, HttpPayload payload) throws IOException {
		return postRequest(url, JSON_CONTENT_TYPE, JSON_CONTENT_TYPE, payload, NO_OP);
	}
	
	public static HttpResponse urlEncodedPostRequest(URL url, String acceptType, Map<String, Object> parameters) throws IOException {
		return postRequest(url, acceptType, URL_ENCODED_CONTENT_TYPE, HttpPayload.fromString(createUrlEncodedParameters(parameters, UrlEscapers.urlFormParameterEscaper())), NO_OP);
	}
	
	public static HttpResponse bearerAuthorizationJsonGetRequest(URL url, String token) throws IOException {
		return getRequest(url, JSON_CONTENT_TYPE, urlConnection -> {
			urlConnection.setRequestProperty("Authorization", "Bearer " + token);
		});
	}
	
	public static HttpResponse postRequest(URL url, String acceptType, String contentType, HttpPayload payload, ConsumerWithIOException<HttpURLConnection> preConnect) throws IOException {
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
		});
	}
	
	public static HttpResponse getRequest(URL url, String acceptType, ConsumerWithIOException<HttpURLConnection> preConnect) throws IOException {
		return basicRequest(url, "GET", acceptType, preConnect, NO_OP);
	}
	
	public static HttpResponse basicRequest(URL url, String method, String acceptType, ConsumerWithIOException<HttpURLConnection> preConnect, ConsumerWithIOException<HttpURLConnection> postConnect) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(15000);
		urlConnection.setReadTimeout(15000);
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
		} catch (IOException ex) {
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
		
		appendUrlEncodedParameters(builder, parameters, UrlEscapers.urlFragmentEscaper());
		
		return new URL(builder.toString());
	}
	
	private static String createUrlEncodedParameters(Map<String, Object> parameters, Escaper escaper) {
		final StringBuilder builder = new StringBuilder();
		appendUrlEncodedParameters(builder, parameters, escaper);
		return builder.toString();
	}
	
	private static StringBuilder appendUrlEncodedParameters(StringBuilder builder, Map<String, Object> parameters, Escaper escaper) {
		boolean needAnd = false;
		for (final Map.Entry<String, Object> entry : parameters.entrySet()) {
			if (needAnd) {
				builder.append("&");
			}
			needAnd = true;
			builder.append(escape(entry.getKey(), escaper));
			builder.append("=");
			builder.append(escape(entry.getValue(), escaper));
		}
		return builder;
	}
	
	private static String escape(Object object, Escaper escaper) {
		return escaper.escape(object.toString());
	}
	
}
