package com.pax.market.android.app.sdk.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.pax.market.android.app.sdk.util.StringUtils;

/**
 * Created by fojut on 2019/1/7.
 */
public class StoreProxyInfo implements Parcelable {

    private int type;   //0:DIRECT, 1:HTTP, 2:SOCKS
    private String host;
    private int port;
    private String authorization;
    private String username;
    private char[] password;

    public StoreProxyInfo() {
    }

    protected StoreProxyInfo(Parcel in) {
        type = in.readInt();
        host = in.readString();
        port = in.readInt();
        authorization = in.readString();
        username = in.readString();
        password = in.createCharArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(host);
        dest.writeInt(port);
        dest.writeString(authorization);
        dest.writeString(username);
        dest.writeCharArray(password);
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getPasswordString() {
        if (password == null) {
            return null;
        }
        return String.valueOf(password);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof StoreProxyInfo))
            return false;

        StoreProxyInfo c = (StoreProxyInfo) obj;
        if (type != c.getType()) {
            return false;
        }
        if (!StringUtils.equals(host, c.getHost())) {
            return false;
        }
        if (port != c.getPort()) {
            return false;
        }
        if (!StringUtils.equals(authorization, c.getAuthorization())) {
            return false;
        }
        if (!StringUtils.equals(username, c.getUsername())) {
            return false;
        }
        if (StringUtils.equals(getPasswordString(), c.getPasswordString())) {
            return false;
        }
        return true;
    }
}
