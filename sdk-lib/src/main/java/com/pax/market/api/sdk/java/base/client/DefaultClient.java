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
package com.pax.market.api.sdk.java.base.client;


import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.CryptoUtils;
import com.pax.market.api.sdk.java.base.util.HttpUtils;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * 客户端
 */
public class DefaultClient {

	private static final Logger logger = LoggerFactory.getLogger(DefaultClient.class.getSimpleName());

    /**
     * The Base url.
     */
    protected String baseUrl;
    /**
     * The App key.
     */
    protected String appKey;
    /**
     * The App secret.
     */
    protected String appSecret;
    /**
     * The Sign method.
     */
    protected String signMethod = Constants.SIGN_METHOD_HMAC;
    /**
     * The Connect timeout.
     */
    protected int connectTimeout = 30000; 			// 默认连接超时时间为30秒
    /**
     * The Read timeout.
     */
    protected int readTimeout = 30000; 				// 默认响应超时时间为30秒

    /**
     * Instantiates a new Default client.
     *
     * @param baseUrl   the base url
     * @param appKey    the app key
     * @param appSecret the app secret
     */
    public DefaultClient(String baseUrl, String appKey, String appSecret) {
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.baseUrl = baseUrl;
	}

    /**
     * Instantiates a new Default client.
     *
     * @param baseUrl        the base url
     * @param appKey         the app key
     * @param appSecret      the app secret
     * @param connectTimeout the connect timeout
     * @param readTimeout    the read timeout
     */
    public DefaultClient(String baseUrl, String appKey, String appSecret, int connectTimeout, int readTimeout) {
		this(baseUrl, appKey, appSecret);
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
	}

    /**
     * Instantiates a new Default client.
     *
     * @param baseUrl        the base url
     * @param appKey         the app key
     * @param appSecret      the app secret
     * @param connectTimeout the connect timeout
     * @param readTimeout    the read timeout
     * @param signMethod     the sign method
     */
    public DefaultClient(String baseUrl, String appKey, String appSecret, int connectTimeout, int readTimeout, String signMethod) {
		this(baseUrl, appKey, appSecret, connectTimeout, readTimeout);
		this.signMethod = signMethod;
	}

    /**
     * Execute string.
     *
     * @param request the request
     * @return the string
     */
    public String execute(SdkRequest request) {
		try {
			return _execute(request);
		} catch (IOException e) {
			logger.error("IOException occurred when execute request. Details: {}", e.toString());
		} catch (GeneralSecurityException e) {
			logger.error("GeneralSecurityException occurred when execute request. Details: {}", e.toString());
		}
		return JsonUtils.getSdkJson(ResultCode.SDK_RQUEST_EXCEPTION);
	}

	private String _execute(SdkRequest request) throws IOException, GeneralSecurityException {
		String response;

		if(appKey != null) {
			request.addRequestParam(Constants.APP_KEY, appKey);
		}
		Long timestamp = request.getTimestamp();
		if(timestamp == null){
			timestamp = System.currentTimeMillis();
		}
		request.addRequestParam(Constants.TIMESTAMP, Long.toString(timestamp));

		String query = HttpUtils.buildQuery(request.getRequestParams(), Constants.CHARSET_UTF8);
		if(appSecret != null) {
			String signature = CryptoUtils.signRequest(query, request.getRequestBody(), appSecret, signMethod);
			request.addHeader(Constants.SIGNATURE, signature);
		}
		String requestUrl = HttpUtils.buildRequestUrl(baseUrl + request.getRequestMappingUrl(), query);
		logger.info(" --> {} {}", request.getRequestMethod().getValue(), requestUrl);

		if(!request.isCompressData()){
			response = HttpUtils.request(requestUrl, request.getRequestMethod().getValue(), connectTimeout, readTimeout, request.getRequestBody(), request.getHeaderMap(), request.getSaveFilePath());
		} else {
			response = HttpUtils.compressRequest(requestUrl, request.getRequestMethod().getValue(), connectTimeout, readTimeout, request.getRequestBody(), request.getHeaderMap(), request.getSaveFilePath());
		}
		return response;
	}

    /**
     * 设置API请求的连接超时时间
     *
     * @param connectTimeout the connect timeout
     */
    public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

    /**
     * 设置API请求的读超时时间
     *
     * @param readTimeout the read timeout
     */
    public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

}
