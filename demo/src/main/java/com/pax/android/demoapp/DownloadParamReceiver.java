package com.pax.android.demoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.pax.market.android.app.sdk.ParamService;

/**
 * Created by zcy on 2016/12/2 0002.
 */
public class DownloadParamReceiver extends BroadcastReceiver {
    private static long lastReceiveTime = -1L;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (isDuplicate(intent)) {
            return;
        }

        //todo add log to see if the broadcast is received, if not, please check whether the bradcast config is correct
        Log.i("DownloadParamReceiver", "broadcast received");
        //todo receive the broadcast from paxstore, start an IntentService to download parameter files
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //
            context.startForegroundService(new Intent(context, DownloadParamService.class));
        } else {
            context.startService(new Intent(context, DownloadParamService.class));
        }
    }

    /**
     * From PAXSTORE client version 7.2.1, you need below function to escape duplicate receiving of parmas.
     *
     * @param intent
     * @return
     */
    private boolean isDuplicate(Intent intent) {
        long receiveTime = (Long) intent.getLongExtra(ParamService.TERMINAL_SEND_TIME, -1L);
        if (receiveTime > 0 && Long.compare(receiveTime, lastReceiveTime) == 0) {
            Log.w("DownloadParamReceiver", "Duplicated! ");
            return true;
        } else {
            lastReceiveTime = receiveTime;
        }
        return false;
    }
}
