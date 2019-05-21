package com.pax.market.android.app.sdk.dto;

import com.pax.market.api.sdk.java.base.dto.SdkObject;

/**
 * Created by zcy on 2019/4/29 0029.
 */

public class LocationInfo extends SdkObject{
    private String longitude;
    private String latitude;
    private Long lastLocateTime;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Long getLastLocateTime() {
        return lastLocateTime;
    }

    public void setLastLocateTime(Long lastLocateTime) {
        this.lastLocateTime = lastLocateTime;
    }

    @Override
    public String toString() {
        return super.toString() + "LocationInfo{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", lastLocateTime=" + lastLocateTime +
                '}';
    }
}
