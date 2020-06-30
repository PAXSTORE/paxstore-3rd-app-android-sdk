package com.pax.market.android.app.sdk;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pax.market.android.app.sdk.util.NotificationUtils;

/**
 * Created by zhangcy on 2020/5/20.
 */
public class ParamService extends IntentService {
    private static final String TAG = ParamService.class.getSimpleName();
    private static final String ACTION_TO_DOWNLOAD_PARAMS = "com.sdk.ACTION_TO_DOWNLOAD_PARAMS";
    public static final String TERMINAL_SERIALNUM = "SN";
    public static final String TERMINAL_SEND_TIME = "SEND_TIME";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ParamService(String name) {
        super(name);
    }

    public ParamService() {
        super(TAG);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        NotificationUtils.showForeGround(this, "Param Service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        String sn = (String) intent.getSerializableExtra(TERMINAL_SERIALNUM);
        Long timeStamp = (Long) intent.getLongExtra(TERMINAL_SEND_TIME, -1L);

        if (sn == null || timeStamp < 0) {
            Log.d(TAG, "sn == null or timeStamp < 0:" + (sn == null || timeStamp < 0));
            return;
        }

        sendBroadcast(new Intent(ACTION_TO_DOWNLOAD_PARAMS)
                .addCategory(getPackageName())
                .setPackage(getPackageName())
                .putExtra(TERMINAL_SERIALNUM, sn));

    }
}
