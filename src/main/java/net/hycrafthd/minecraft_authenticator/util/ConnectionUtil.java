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
	
	public static HttpResponse jsonRequest(URL url, HttpPayload payload) throws IOException {
		return postRequest(url, JSON_CONTENT_TYPE, JSON_CONTENT_TYPE, payload);
	}
	
	public static HttpResponse urlEncodedRequest(URL url, String acceptType, Map<String, Object> parameters) throws IOException {
		return postRequest(url, URL_ENCODED_CONTENT_TYPE, acceptType, HttpPayload.fromString(appendUrlEncodedParameters(new StringBuilder(), parameters, UrlEscapers.urlFormParameterEscaper()).toString()));
	}
	
	public static HttpResponse postRequest(URL url, String contentType, String acceptType, HttpPayload payload) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(15000);
		urlConnection.setReadTimeout(15000);
		urlConnection.setUseCaches(false);
		urlConnection.setInstanceFollowRedirects(true);
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
		urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		urlConnection.setRequestProperty("Accept", acceptType);
		urlConnection.setRequestProperty("User-Agent", Constants.USER_AGENT);
		urlConnection.setRequestProperty("Charset", StandardCharsets.UTF_8.name());
		urlConnection.setRequestProperty("Content-Type", contentType);
		urlConnection.setFixedLengthStreamingMode(payload.getSize());
		
		urlConnection.connect();
		
		if (payload.hasContent()) {
			try (final OutputStream outputStream = urlConnection.getOutputStream()) {
				payload.write(outputStream);
			}
		}
		
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
