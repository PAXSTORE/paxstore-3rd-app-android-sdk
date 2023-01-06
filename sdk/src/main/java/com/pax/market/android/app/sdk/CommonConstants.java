package com.pax.market.android.app.sdk;

/**
 * Created by zcy on 2019/5/21 0021.
 */

public class CommonConstants {

    public static final String ERR_MSG_BIND_PAXSTORE_SERVICE_FAILED =
            "Bind service failed, STORE client may not running or STORE client version is below 6.1. Please check";
    public static final String ERR_MSG_BIND_PAXSTORE_SERVICE_TOO_FAST =
            "Bind service failed, get terminal infomation too fast";
    public static final String ERR_MSG_NULL_RETURNED = "Null value returned, STORE client may not activated or running. Please check";
    public static final String ERR_MSG_PAXSTORE_MAY_NOT_INSTALLED = "Bind service failed, STORE client may not installed";

    public static final String MEDIA_PATH = "/adCache/img_full.png";

    public static final String SP_LAST_GET_TERMINAL_INFO_TIME =  "sp_last_get_terminal_time";
    public static final String SP_LAST_GET_ONLINE_STATUS_TIME =  "sp_last_get_online_time";
    public static final String SP_LAST_GET_LOCATION_TIME =  "sp_last_get_location_time";
    public static final String SP_LAST_GET_MERCHANT_TIME =  "sp_last_get_merchant_time";

    public static final String SP_LAST_GET_DCURL_TIME =  "sp_last_get_dcurl_time";

    public static final String SP_SMALL_LOGO_ICON = "sp_small_logo_icon";

    public static final long ONE_HOUR_INTERVAL =  3600_000L;

    public static final String ACTION_START_CUSTOMER_SERVICE = "com.sdk.service.ACTION_TO_DOWNLOAD_PARAMS";

}
