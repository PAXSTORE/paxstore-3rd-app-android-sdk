/*
 * ******************************************************************************
 * COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *
 *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 * ******************************************************************************
 */

package com.pax.market.api.sdk.java.api.sync;

import com.google.gson.Gson;
import com.pax.market.api.sdk.java.api.sync.dto.TerminalSyncInfo;
import com.pax.market.api.sdk.java.base.api.BaseApi;
import com.pax.market.api.sdk.java.base.client.DefaultClient;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.request.SdkRequest;
import com.pax.market.api.sdk.java.base.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanjun on 2017/12/15.
 */
public class SyncApi extends BaseApi {

    /**
     * The constant downloadParamUrl.
     */
    protected static String syncTerminalInfoUrl = "/3rdApps/info";

    public SyncApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }

    public interface SyncType {
        /**
         * Application Info
         */
        int APPLICATION = 1;

        /**
         * Device Info
         */
        int DEVICE = 2;

        /**
         * Hardware Info
         */
        int HARDWARE = 3;

        /**
         * Application Install History
         */
        int INSTALL_HISTORY = 4;
    }

    /**
     * 同步终端信息
     *
     * @return Json result string
     */
    public SdkObject syncTerminalInfo(List<TerminalSyncInfo> infoList){
        DefaultClient client = new DefaultClient(getBaseUrl(), getAppKey(), getAppSecret());
        SdkRequest request = new SdkRequest(syncTerminalInfoUrl);
        request.setRequestMethod(SdkRequest.RequestMethod.POST);
        request.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
        request.addHeader(Constants.REQ_HEADER_SN, getTerminalSN());
        request.setRequestBody(new Gson().toJson(infoList, ArrayList.class));
        return JsonUtils.fromJson(client.execute(request), SdkObject.class);
    }

}
