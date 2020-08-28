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

    /**
     * Ignore tasks within 3 seconds
     */
    public static long TIME_FILTER = 3_000L;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isDuplicate(intent)) {
            return;
        }

        Log.i("DownloadParamReceiver", "broadcast received");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(DelayService.getCallingIntent(context));
        } else {
            context.startService(DelayService.getCallingIntent(context));
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
        long receiveTime = intent.getLongExtra(ParamService.TERMINAL_SEND_TIME, -1L);
        if (receiveTime > 0 && receiveTime == timeStamp) {
            return true;
        } else {
            timeStamp = receiveTime;
        }

        lastReceiveTime = System.currentTimeMillis();
        return false;
    }
}
