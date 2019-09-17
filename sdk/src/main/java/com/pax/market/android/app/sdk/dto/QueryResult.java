package com.pax.market.android.app.sdk.dto;

/**
 * Created by zcy on 2019/4/30 0030.
 */

public enum QueryResult {

    SUCCESS(0, "success"),
    GET_LOCATION_FAILED(-1, "Get location failed"),
    INIT_LOCATIONMANAGER_FAILED(-2, "Init LocationManager failed"),
    GET_INFO_NOT_ALLOWED(-3, "Not allowed to get terminal information from PAXSTORE"),
    GET_LOCATION_TOO_FAST(-4, "Get location too fast"),
    PUSH_NOT_ENABLED(-5, "Push not enabled"),
    QUERY_FROM_CONTENT_PROVIDER_FAILED(-6, "Query failed"),
    UNKNOWN(-10, "unknown");

    private final String msg;
    private final int code;

    QueryResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public QueryResult getByCode(int code) {
        for (QueryResult queryResult : values()) {
            if (queryResult.getCode() == code) {
                return queryResult;
            }
        }
        return UNKNOWN;
    }

    public QueryResult getByMsg(String msg) {
        for (QueryResult queryResult : values()) {
            if (queryResult.getMsg().equals(msg)) {
                return queryResult;
            }
        }
        return UNKNOWN;
    }
}
