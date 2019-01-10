package com.pax.market.android.app.sdk.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fojut on 2019/1/7.
 */
public class StoreProxyInfo implements Parcelable {

    private int type;   //0:DIRECT, 1:HTTP
    private String host;
    private int port;
    private String authorization;

    public StoreProxyInfo() {
    }

    protected StoreProxyInfo(Parcel in) {
        type = in.readInt();
        host = in.readString();
        port = in.readInt();
        authorization = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(host);
        dest.writeInt(port);
        dest.writeString(authorization);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StoreProxyInfo> CREATOR = new Creator<StoreProxyInfo>() {
        @Override
        public StoreProxyInfo createFromParcel(Parcel in) {
            return new StoreProxyInfo(in);
        }

        @Override
        public StoreProxyInfo[] newArray(int size) {
            return new StoreProxyInfo[size];
        }
    };

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StoreProxyInfo))
            return false;

        StoreProxyInfo c = (StoreProxyInfo) obj;
        if(c.toString().equals(toString())){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("StoreProxyInfo");
        builder.append("{type=")
                .append(type)
                .append(", host=")
                .append(host)
                .append(", port=")
                .append(port)
                .append(", authorization=")
                .append(authorization)
                .append("}");
        return builder.toString();
    }
}
