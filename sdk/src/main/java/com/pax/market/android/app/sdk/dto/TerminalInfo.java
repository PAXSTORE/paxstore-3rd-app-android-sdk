package com.pax.market.android.app.sdk.dto;

import android.os.Parcel;
import android.os.Parcelable;


public class TerminalInfo implements Parcelable{

    public TerminalInfo(){}

    private String tid;
    private String terminalName;
    private String serialNo;
    private String modelName;
    private String factoryName; //manufactory
    private String merchantName;
    private int statusCode; //0:online; -1:offline

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }


    public TerminalInfo(Parcel in) {
        tid = in.readString();
        terminalName = in.readString();
        serialNo = in.readString();
        modelName = in.readString();
        factoryName = in.readString();
        merchantName = in.readString();
        statusCode = in.readInt();
    }

    public static final Creator<TerminalInfo> CREATOR = new Creator<TerminalInfo>() {
        @Override
        public TerminalInfo createFromParcel(Parcel in) {
            return new TerminalInfo(in);
        }

        @Override
        public TerminalInfo[] newArray(int size) {
            return new TerminalInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tid);
        dest.writeString(terminalName);
        dest.writeString(serialNo);
        dest.writeString(modelName);
        dest.writeString(factoryName);
        dest.writeString(merchantName);
        dest.writeInt(statusCode);
    }

    /**
     * 参数是一个Parcel,用它来存储与传输数据
     * @param dest
     */
    public void readFromParcel(Parcel dest) {
        //注意，此处的读值顺序应当是和writeToParcel()方法中一致的
        tid = dest.readString();
        terminalName = dest.readString();
        serialNo = dest.readString();
        modelName = dest.readString();
        factoryName = dest.readString();
        merchantName = dest.readString();
        statusCode = dest.readInt();
    }

    @Override
    public String toString() {
        return "TerminalInfo{" +
                "tid='" + tid + '\'' +
                ", terminalName='" + terminalName + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", modelName='" + modelName + '\'' +
                ", factoryName='" + factoryName + '\'' +
                ", merchantName='" + merchantName + '\'' +
                ", statusCode='" + statusCode + '\'' +
                '}';
    }
}
