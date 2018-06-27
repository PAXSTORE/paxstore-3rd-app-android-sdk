/*
 * *
 *     * ********************************************************************************
 *     * COPYRIGHT
 *     *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *     *   This software is supplied under the terms of a license agreement or
 *     *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *     *   or disclosed except in accordance with the terms in that agreement.
 *     *
 *     *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 *     * ********************************************************************************
 *
 */

package com.pax.market.api.sdk.java.base.dto;

/**
 * Created by zcy on 2016/12/6 0006.
 */
public class UpdateActionObject {
    /**
     * ID of push task
     */
    private Long actionId;
    /**
     * status of push task
     */
    private int status;
    /**
     * error code of push task
     */
    private int errorCode;
    /**
     * remarks of push task
     */
    private String remarks;

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
