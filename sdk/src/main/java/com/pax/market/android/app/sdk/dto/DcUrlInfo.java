package com.pax.market.android.app.sdk.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fojut on 2019/1/7.
 */
public class DcUrlInfo implements Parcelable {

    private String dcUrl;
    private long lastAccessTime;
    private int businessCode;
    private String message;
    private String staticUrl;

    public DcUrlInfo() {
    }

    protected DcUrlInfo(Parcel in) {
        dcUrl = in.readString();
        lastAccessTime = in.readLong();
        businessCode = in.readInt();
        message = in.readString();
        staticUrl = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dcUrl);
        dest.writeLong(lastAccessTime);
        dest.writeInt(businessCode);
        dest.writeString(message);
        dest.writeString(staticUrl);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DcUrlInfo> CREATOR = new Creator<DcUrlInfo>() {
        @Override
        public DcUrlInfo createFromParcel(Parcel in) {
            return new DcUrlInfo(in);
        }

        @Override
        public DcUrlInfo[] newArray(int size) {
            return new DcUrlInfo[size];
        }
    };

    public int getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(int businessCode) {
        this.businessCode = businessCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDcUrl() {
        return dcUrl;
    }

    public void setDcUrl(String dcUrl) {
        this.dcUrl = dcUrl;
    }


    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getStaticUrl() {
        return staticUrl;
    }

    public void setStaticUrl(String staticUrl) {
        this.staticUrl = staticUrl;
    }

    @Override
    public String toString() {
        return "DcUrlInfo{" +
                "dcUrl='" + dcUrl + '\'' +
                ", lastAccessTime=" + lastAccessTime +
                ", businessCode=" + businessCode +
                ", message='" + message + '\'' +
                ", staticUrl='" + staticUrl + '\'' +
                '}';
    }
}
