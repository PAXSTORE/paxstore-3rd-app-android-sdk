/*
 * *******************************************************************************
 * COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *
 *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 * *******************************************************************************
 */
package com.pax.market.api.sdk.java.base.util;


import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.SdkObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * 网络工具类。
 */
public abstract class HttpUtils {
	private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

	private static final int BUFFER_SIZE = 4096;
	private static final String DEFAULT_CHARSET = Constants.CHARSET_UTF8;
	private static Locale locale = Locale.CHINA;

    /**
     * Sets local.
     *
     * @param locale the locale
     */
    public static void setLocal(Locale locale) {
		HttpUtils.locale = locale;
	}

    /**
     * The type Trust all trust manager.
     */
    public static class TrustAllTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	}

	private HttpUtils() {
	}

    /**
     * Request string.
     *
     * @param requestUrl     the request url
     * @param requestMethod  the request method
     * @param connectTimeout the connect timeout
     * @param readTimeout    the read timeout
     * @param userData       the user data
     * @param headerMap      the header map
     * @return the string
     */
    public static String request(String requestUrl, String requestMethod, int connectTimeout, int readTimeout, String userData, Map<String, String> headerMap){
		return request(requestUrl, requestMethod, connectTimeout, readTimeout, userData, false, headerMap, null);
	}

    /**
     * Compress request string.
     *
     * @param requestUrl     the request url
     * @param requestMethod  the request method
     * @param connectTimeout the connect timeout
     * @param readTimeout    the read timeout
     * @param userData       the user data
     * @param headerMap      the header map
     * @return the string
     */
    public static String compressRequest(String requestUrl, String requestMethod, int connectTimeout, int readTimeout, String userData, Map<String, String> headerMap){
		return request(requestUrl, requestMethod, connectTimeout, readTimeout, userData, true, headerMap, null);
	}

    /**
     * Request string.
     *
     * @param requestUrl     the request url
     * @param requestMethod  the request method
     * @param connectTimeout the connect timeout
     * @param readTimeout    the read timeout
     * @param userData       the user data
     * @param headerMap      the header map
     * @param saveFilePath   the save file path
     * @return the string
     */
    public static String request(String requestUrl, String requestMethod, int connectTimeout, int readTimeout, String userData, Map<String, String> headerMap, String saveFilePath){
		return request(requestUrl, requestMethod, connectTimeout, readTimeout, userData, false, headerMap, saveFilePath);
	}

    /**
     * Compress request string.
     *
     * @param requestUrl     the request url
     * @param requestMethod  the request method
     * @param connectTimeout the connect timeout
     * @param readTimeout    the read timeout
     * @param userData       the user data
     * @param headerMap      the header map
     * @param saveFilePath   the save file path
     * @return the string
     */
    public static String compressRequest(String requestUrl, String requestMethod, int connectTimeout, int readTimeout, String userData, Map<String, String> headerMap, String saveFilePath){
		return request(requestUrl, requestMethod, connectTimeout, readTimeout, userData, true, headerMap, saveFilePath);
	}

	private static String request(String requestUrl, String requestMethod, int connectTimeout, int readTimeout, String userData, boolean compressData,
								  Map<String, String> headerMap, String saveFilePath) {
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = getConnection(requestUrl, connectTimeout, readTimeout);
			return finalRequest(urlConnection, requestMethod, userData, compressData, headerMap, saveFilePath);
		} catch (IOException e) {
			logger.error("IOException Occurred. Details: {}", e.toString());
			return JsonUtils.getSdkJson(ResultCode.SDK_RQUEST_EXCEPTION, e.getMessage());
		} finally {
			if(urlConnection != null) {
				urlConnection.disconnect();
			}
		}
	}

	private static String finalRequest(HttpURLConnection urlConnection, String requestMethod, String userData, boolean compressData,
									   Map<String, String> headerMap, String saveFilePath) {
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		FileOutputStream fileOutputStream = null;
		String filePath = null;
		try {
			urlConnection.setDoInput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestMethod(requestMethod);
			if(locale != null) {
				urlConnection.setRequestProperty(Constants.ACCESS_LANGUAGE, getLanguageTag(locale));
			}
			if (headerMap != null) {
				for (Entry<String, String> entry : headerMap.entrySet()) {
					urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			if ("GET".equalsIgnoreCase(requestMethod) || "DELETE".equalsIgnoreCase(requestMethod)) {
				urlConnection.connect();
			} else {
				urlConnection.setDoOutput(true);
			}
			if ((null != userData) && (userData.length() > 0)) {
				OutputStream outputStream = null;
				try {
					outputStream = urlConnection.getOutputStream();
					if (!compressData) {
						outputStream.write(userData.getBytes("UTF-8"));
					} else {
						String hexString = AlgHelper.bytes2HexString(compressData(userData.getBytes("UTF-8")));
						outputStream.write(hexString.getBytes());
					}
				} finally {
					if (outputStream != null) {
						outputStream.close();
					}
				}
			}

			if(saveFilePath != null) {
				filePath = saveFilePath + File.separator + FileUtils.generateMixString(16) ;

				File fileDir = new File(saveFilePath);
				if(!fileDir.exists()) {
					fileDir.mkdirs();
				}
				fileOutputStream = new FileOutputStream(filePath);

				int bytesRead;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = urlConnection.getInputStream().read(buffer)) != -1) {
					fileOutputStream.write(buffer, 0, bytesRead);
				}



				return JsonUtils.getSdkJson(ResultCode.SUCCESS, filePath);
			}

			if (urlConnection.getResponseCode() == 200) {
				bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
			} else {
				if(urlConnection.getErrorStream() != null) {
					bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream(), "utf-8"));
				} else {
					bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
				}
			}

			String str;
			while ((str = bufferedReader.readLine()) != null) {
				stringBuilder.append(str);
			}

			return stringBuilder.toString();

		} catch (SocketTimeoutException localSocketTimeoutException) {
			FileUtils.deleteFile(filePath);
			logger.error("SocketTimeoutException Occurred. Details: {}", localSocketTimeoutException.toString());
			return JsonUtils.getSdkJson(ResultCode.SDK_CONNECT_TIMEOUT);
		} catch (ConnectException localConnectException) {
			FileUtils.deleteFile(filePath);
			logger.error("ConnectException Occurred. Details: {}", localConnectException.toString());
			return JsonUtils.getSdkJson(ResultCode.SDK_UN_CONNECT);
		} catch (FileNotFoundException fileNotFoundException) {
			FileUtils.deleteFile(filePath);
			logger.error("FileNotFoundException Occurred. Details: {}", fileNotFoundException.toString());
			return JsonUtils.getSdkJson(ResultCode.SDK_FILE_NOT_FOUND);
		} catch (Exception ignored) {
			FileUtils.deleteFile(filePath);
			logger.error("Exception Occurred. Details: {}", ignored.toString());
			return JsonUtils.getSdkJson(ResultCode.SDK_RQUEST_EXCEPTION, ignored.getMessage());
		} finally {
			if(bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error("IOException Occurred. Details: {}", e.toString());
				}
			}
			if(fileOutputStream != null){
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					logger.error("IOException Occurred. Details: {}", e.toString());
				}
			}
			if(urlConnection != null) {
				urlConnection.disconnect();
			}
		}
	}

	private static HttpURLConnection getConnection(String requestUrl, int connectTimeout, int readTimeout) throws IOException {
		URL url = new URL(requestUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (conn instanceof HttpsURLConnection) {
			HttpsURLConnection connHttps = (HttpsURLConnection) conn;
			try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null, new TrustManager[] { new TrustAllTrustManager() }, new SecureRandom());
				connHttps.setSSLSocketFactory(ctx.getSocketFactory());
				connHttps.setHostnameVerifier(new HostnameVerifier() {
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				});
			} catch (GeneralSecurityException e){
				logger.error("GeneralSecurityException Occurred. Details: {}", e.toString());
			}
			connHttps.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			conn = connHttps;
		}

		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		return conn;
	}

	private static URL buildGetUrl(String url, String query) throws IOException {
		if (StringUtils.isEmpty(query)) {
			return new URL(url);
		}

		return new URL(buildRequestUrl(url, query));
	}

    /**
     * Build request url string.
     *
     * @param url     the url
     * @param queries the queries
     * @return the string
     */
    public static String buildRequestUrl(String url, String... queries) {
		if (queries == null || queries.length == 0) {
			return url;
		}

		StringBuilder newUrl = new StringBuilder(url);
		boolean hasQuery = url.contains("?");
		boolean hasPrepend = url.endsWith("?") || url.endsWith("&");

		for (String query : queries) {
			if (!StringUtils.isEmpty(query)) {
				if (!hasPrepend) {
					if (hasQuery) {
						newUrl.append("&");
					} else {
						newUrl.append("?");
						hasQuery = true;
					}
				}
				newUrl.append(query);
				hasPrepend = false;
			}
		}
		return newUrl.toString();
	}

    /**
     * Build query string.
     *
     * @param params  the params
     * @param charset the charset
     * @return the string
     * @throws IOException the io exception
     */
    public static String buildQuery(Map<String, String> params, String charset) throws IOException {
		if (params == null || params.isEmpty()) {
			return null;
		}

		StringBuilder query = new StringBuilder();
		Set<Entry<String, String>> entries = params.entrySet();
		boolean hasParam = false;

		for (Entry<String, String> entry : entries) {
			String name = entry.getKey();
			String value = entry.getValue();
			// 忽略参数名或参数值为空的参数
			if (StringUtils.areNotEmpty(name, value)) {
				if (hasParam) {
					query.append("&");
				} else {
					hasParam = true;
				}

				query.append(name).append("=").append(URLEncoder.encode(value, charset));
			}
		}

		return query.toString();
	}

    /**
     * Gets response as string.
     *
     * @param conn the conn
     * @return the response as string
     * @throws IOException the io exception
     */
    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
		String charset = getResponseCharset(conn.getContentType());
		if (conn.getResponseCode() < 400) {
			String contentEncoding = conn.getContentEncoding();
			if (Constants.CONTENT_ENCODING_GZIP.equalsIgnoreCase(contentEncoding)) {
				return getStreamAsString(new GZIPInputStream(conn.getInputStream()), charset);
			} else {
				return getStreamAsString(conn.getInputStream(), charset);
			}
		} else {// Client Error 4xx and Server Error 5xx
			throw new IOException(conn.getResponseCode() + " " + conn.getResponseMessage());
		}
	}

    /**
     * Gets stream as string.
     *
     * @param stream  the stream
     * @param charset the charset
     * @return the stream as string
     * @throws IOException the io exception
     */
    public static String getStreamAsString(InputStream stream, String charset) throws IOException {
		try {
			Reader reader = new InputStreamReader(stream, charset);
			StringBuilder response = new StringBuilder();

			final char[] buff = new char[1024];
			int read = 0;
			while ((read = reader.read(buff)) > 0) {
				response.append(buff, 0, read);
			}

			return response.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

    /**
     * Gets response charset.
     *
     * @param ctype the ctype
     * @return the response charset
     */
    public static String getResponseCharset(String ctype) {
		String charset = DEFAULT_CHARSET;

		if (!StringUtils.isEmpty(ctype)) {
			String[] params = ctype.split(";");
			for (String param : params) {
				param = param.trim();
				if (param.startsWith("charset")) {
					String[] pair = param.split("=", 2);
					if (pair.length == 2) {
						if (!StringUtils.isEmpty(pair[1])) {
							charset = pair[1].trim();
						}
					}
					break;
				}
			}
		}

		return charset;
	}

    /**
     * 使用默认的UTF-8字符集反编码请求参数值。
     *
     * @param value 参数值
     * @return 反编码后的参数值 string
     */
    public static String decode(String value) {
		return decode(value, DEFAULT_CHARSET);
	}

    /**
     * 使用默认的UTF-8字符集编码请求参数值。
     *
     * @param value 参数值
     * @return 编码后的参数值 string
     */
    public static String encode(String value) {
		return encode(value, DEFAULT_CHARSET);
	}

    /**
     * 使用指定的字符集反编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 反编码后的参数值 string
     */
    public static String decode(String value, String charset) {
		String result = null;
		if (!StringUtils.isEmpty(value)) {
			try {
				result = URLDecoder.decode(value, charset);
			} catch (IOException e) {
				logger.error("IOException Occurred. Details: {}", e.toString());
			}
		}
		return result;
	}

    /**
     * 使用指定的字符集编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 编码后的参数值 string
     */
    public static String encode(String value, String charset) {
		String result = null;
		if (!StringUtils.isEmpty(value)) {
			try {
				result = URLEncoder.encode(value, charset);
			} catch (IOException e) {
				logger.error("IOException Occurred. Details: {}", e.toString());
			}
		}
		return result;
	}

    /**
     * 从URL中提取所有的参数。
     *
     * @param query URL地址
     * @return 参数映射 map
     */
    public static Map<String, String> splitUrlQuery(String query) {
		Map<String, String> result = new HashMap<String, String>();

		String[] pairs = query.split("&");
		if (pairs != null && pairs.length > 0) {
			for (String pair : pairs) {
				String[] param = pair.split("=", 2);
				if (param != null && param.length == 2) {
					result.put(param[0], param[1]);
				}
			}
		}

		return result;
	}

    /**
     * Compress data byte [ ].
     *
     * @param bytes the bytes
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    public static byte[] compressData(byte[] bytes)
			throws IOException {
		if (null == bytes) {
			return null;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
		gzipOutputStream.write(bytes);
		gzipOutputStream.close();
		return byteArrayOutputStream.toByteArray();
	}

	private static String getLanguageTag(Locale locale) {
		if (locale != null) {
			String localeStr = locale.toString();
			return localeStr.replace("_", "-");
		}
		return null;
	}



}
