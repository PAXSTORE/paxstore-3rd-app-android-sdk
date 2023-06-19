package com.pax.market.android.app.sdk.dto;

import android.os.Parcel;
import android.os.Parcelable;


public class TerminalInfo implements Parcelable{

    public TerminalInfo(){}

    private int bussinessCode;
    private String message;
    private String tid;
    private String terminalName;
    private String serialNo;
    private String modelName;
    private String factoryName; //manufactory
    private String merchantName;
    private int statusCode; //0:online; -1:offline


    public int getBussinessCode() {
        return bussinessCode;
    }

    public void setBussinessCode(int bussinessCode) {
        this.bussinessCode = bussinessCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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


    public TerminalInfo(Parcel in) {
        tid = in.readString();
        terminalName = in.readString();
        serialNo = in.readString();
        modelName = in.readString();
        factoryName = in.readString();
        merchantName = in.readString();
        statusCode = in.readInt();
        bussinessCode = in.readInt();
        message = in.readString();
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

    /**
     * Note: The new field can only be added at the end, otherwise it will affect the reading of the existing SDK
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tid);
        dest.writeString(terminalName);
        dest.writeString(serialNo);
        dest.writeString(modelName);
        dest.writeString(factoryName);
        dest.writeString(merchantName);
        dest.writeInt(statusCode);
        dest.writeInt(bussinessCode);
        dest.writeString(message);
    }

    /**
     * The parameter is a Parcel, use it to store and transmit data
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
        bussinessCode = dest.readInt();
        message = dest.readString();
    }

    @Override
    public String toString() {
        return "TerminalInfo{" +
                "bussinessCode=" + bussinessCode +
                ", message='" + message + '\'' +
                ", tid='" + tid + '\'' +
                ", terminalName='" + terminalName + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", modelName='" + modelName + '\'' +
                ", factoryName='" + factoryName + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
