package com.pax.market.api.sdk.java.base.dto;


import com.google.gson.annotations.SerializedName;


/**
 * Created by zhangchenyang on 2018/5/16.
 */
public class DownloadResultObject extends SdkObject {

    /**
     * path that param files saved
     */
    @SerializedName("paramSavePath")
    String paramSavePath;

    public String getParamSavePath() {
        return paramSavePath;
    }

    public void setParamSavePath(String paramSavePath) {
        this.paramSavePath = paramSavePath;
    }

    @Override
    public String toString() {
        return "DownloadResultObject{" +
                "paramSavePath='" + paramSavePath + '\'' +
                '}';
    }
}
