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
package com.pax.market.api.sdk.java.base.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fanjun on 2016/11/10.
 */
public class SdkObject implements Serializable {
    private static final long serialVersionUID = 1L;

    @SerializedName("businessCode")
    private int businessCode = -1;
    @SerializedName("message")
    private String message;

    /**
     * Gets result code.
     *
     * @return the result code
     */
    public int getBusinessCode() {
        return businessCode;
    }

    /**
     * Sets result code.
     *
     * @param businessCode the result code
     */
    public void setBusinessCode(int businessCode) {
        this.businessCode = businessCode;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        if(message == null || "".equals(message.trim())) {
            return String.valueOf(businessCode);
        }
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SdkObject{" +
                "businessCode=" + businessCode +
                ", message='" + message + '\'' +
                '}';
    }
}
