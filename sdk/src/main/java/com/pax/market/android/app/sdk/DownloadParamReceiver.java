package com.pax.market.android.app.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by zcy on 2020/06/30 0002.
 */
public class DownloadParamReceiver extends BroadcastReceiver {
    private static long lastReceiveTime = -1L;
    private static long timeStamp = -1L;

    private static String ACTION_START_CUSTOMER_SERVICE = "com.sdk.service.ACTION_TO_DOWNLOAD_PARAMS";
    /**
     * Ignore tasks within 5 seconds
     */
    private static long TIME_FILTER = 5_000L;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isDuplicate(intent)) {
            return;
        }

        Log.i("DownloadParamReceiver", "broadcast received");
        Intent startIntent = new Intent(ACTION_START_CUSTOMER_SERVICE);
        startIntent.setPackage(context.getPackageName());
        startIntent.addCategory(context.getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(startIntent);
        } else {
            context.startService(startIntent);
        }
    }

    /**
     * 1. Ignore tasks within 5 seconds.
     * 2. From PAXSTORE client version 7.2.1, you need below function to escape duplicate receiving of parmas.
     *
     * @param intent
     * @return
     */
    private boolean isDuplicate(Intent intent) {
        // Ignore tasks within 5 seconds.
        if (System.currentTimeMillis() - lastReceiveTime < TIME_FILTER) {
            return true;
        }

        // From PAXSTORE client version 7.2.1, you need below function to escape duplicate receiving of parmas.
        long receiveTime = (Long) intent.getLongExtra(ParamService.TERMINAL_SEND_TIME, -1L);
        if (receiveTime > 0 && Long.compare(receiveTime, timeStamp) == 0) {
            return true;
        } else {
            timeStamp = receiveTime;
        }

        lastReceiveTime = System.currentTimeMillis();
        return false;
    }
}
