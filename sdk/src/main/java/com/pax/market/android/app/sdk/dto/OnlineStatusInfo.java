package com.pax.market.android.app.sdk.dto;

import com.pax.market.api.sdk.java.base.dto.SdkObject;

/**
 * Created by zcy on 2019/4/29 0029.
 */

public class OnlineStatusInfo extends SdkObject {
    private boolean online;

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return super.toString() +
                "OnlineStatusInfo{" +
                "online=" + online +
                '}';
    }
}
