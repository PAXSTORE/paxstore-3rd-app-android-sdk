package com.pax.market.android.app.sdk;

/**
 * Created by fojut on 2019/5/20.
 */
public class PushConstants {

    public static final String PUSH_MESSAGE = "push_message";
    public static final String PUSH_MESSAGE_ID = "push_message_id";
    public static final String PUSH_MESSAGE_TYPE = "push_message_type";

    public static final String ACTION_NOTIFICATION_MESSAGE_RECEIVED = "com.paxstore.mpush.NOTIFICATION_MESSAGE_RECEIVED";
    public static final String ACTION_NOTIFICATION_CLICK = "com.paxstore.mpush.NOTIFICATION_CLICK";
    public static final String ACTION_NOTIFICATION_CANCEL = "com.paxstore.mpush.NOTIFICATION_CANCEL";
    public static final String EXTRA_MESSAGE_NID = "msg_nid";
    public static final String EXTRA_MESSAGE_TITLE = "msg_title";
    public static final String EXTRA_MESSAGE_CONTENT = "msg_content";
    public static final String EXTRA_MEIDA = "msg_media";


    public static final String ACTION_DATA_MESSAGE_RECEIVED = "com.paxstore.mpush.DATA_MESSAGE_RECEIVED";
    public static final String EXTRA_MESSAGE_DATA = "msg_data";

    public static final String ACTION_NOTIFY_DATA_MESSAGE_RECEIVED = "com.paxstore.mpush.NOTIFY_DATA_MESSAGE_RECEIVED";
    public static final String ACTION_NOTIFY_MEDIA_MESSAGE_RECEIVED = "com.paxstore.mpush.NOTIFY_MEDIA_MESSAGE_RECEIVED";

    public static final String MEDIA_MESSAGE = "media_message";
    public static final String MEDIA_MESSAGE_FULL = "media_message_full";
    public static final String MEDIA_MESSAGE_MID = "media_message_mid";
    public static final String MEDIA_MESSAGE_TITLE = "media_message_title";

    public static final int MEDIA_TYPE_FULL = 0;
    public static final int MEDIA_TYPE_MID = 1;
    public static final int MEDIA_TYPE_TITLE = 2;

}
