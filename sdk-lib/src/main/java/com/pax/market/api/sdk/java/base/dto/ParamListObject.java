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

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ParamListObject extends SdkObject {
    /**
     * count of push task
     */
    @SerializedName("totalCount")
    private int totalCount;
    /**
     * list of push tasks detail information
     */
    @SerializedName("list")
    private List<ParamObject> list;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }


    public List<ParamObject> getList() {
        return list;
    }

    public void setList(List<ParamObject> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "ParamListObject{" +
                "totalCount=" + totalCount +
                ", list=" + list +
                '}';
    }
}