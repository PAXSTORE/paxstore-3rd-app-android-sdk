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

package com.pax.market.api.sdk.java.base.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fanjun on 2016/12/5.
 */
public class BaseApi {
    private final Logger logger = LoggerFactory.getLogger(BaseApi.class.getSimpleName());

    /**
     * The constant baseUrl.
     */
    private String baseUrl;
    /**
     * The constant appKey.
     */
    private String appKey;
    /**
     * The constant appSecret.
     */
    private String appSecret;
    /**
     * The constant terminal SN.
     */
    private String terminalSN;

    public BaseApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        this.baseUrl = baseUrl;
        this.appKey = appKey;
        this.appSecret = appSecret;
        this.terminalSN = terminalSN;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getTerminalSN() {
        return terminalSN;
    }
}
