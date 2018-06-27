package com.pax.market.api.sdk.java.base.dto;

import java.util.List;

/**
 * Created by zhangchenyang on 2018/5/17.
 */
public class DownloadResult extends SdkObject {

    private List<DownloadResultObject> downloadResultObjectList;

    public List<DownloadResultObject> getDownloadResultObjectList() {
        return downloadResultObjectList;
    }

    public void setDownloadResultObjectList(List<DownloadResultObject> downloadResultObjectList) {
        this.downloadResultObjectList = downloadResultObjectList;
    }

    @Override
    public String toString() {
        return "DownloadResult{" +
                "downloadResultObjectList=" + downloadResultObjectList +
                '}';
    }
}
