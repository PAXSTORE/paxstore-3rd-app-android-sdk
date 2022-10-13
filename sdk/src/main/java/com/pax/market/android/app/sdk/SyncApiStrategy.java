package com.pax.market.android.app.sdk;

import android.content.Context;
import android.util.Log;

import com.pax.market.android.app.sdk.dto.QueryResult;
import com.pax.market.android.app.sdk.util.PreferencesUtils;
import com.pax.market.api.sdk.java.api.sync.SyncApi;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.LocationObject;

public class SyncApiStrategy extends SyncApi {
    private Context context;

    public SyncApiStrategy(Context context,String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
        this.context = context;
    }

    public LocationObject getLocate() {
        LocationObject locationObject = new LocationObject();
        long lastSdkLocateTime = PreferencesUtils.getLong(context, CommonConstants.SP_LAST_GET_LOCATION_TIME, 0L);
        if (System.currentTimeMillis() - lastSdkLocateTime < 1000L) { //Ignore call within 1 second
            locationObject.setBusinessCode(QueryResult.GET_LOCATION_TOO_FAST.getCode());
            locationObject.setMessage(QueryResult.GET_LOCATION_TOO_FAST.getMsg());
            Log.w("StoreSdk", QueryResult.GET_LOCATION_TOO_FAST.getMsg());
            return locationObject;
        }

        PreferencesUtils.putLong(context, CommonConstants.SP_LAST_GET_LOCATION_TIME, System.currentTimeMillis());

        locationObject = getLocationInfo();
        if (locationObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
            locationObject.setLastLocateTime(System.currentTimeMillis());
            PreferencesUtils.putLong(context, CommonConstants.SP_LAST_LOCATION_SUCCESS_TIME, System.currentTimeMillis());
        } else {
            locationObject.setLastLocateTime(PreferencesUtils.getLong(context, CommonConstants.SP_LAST_LOCATION_SUCCESS_TIME, 0L));
        }
        return locationObject;
    }
}
