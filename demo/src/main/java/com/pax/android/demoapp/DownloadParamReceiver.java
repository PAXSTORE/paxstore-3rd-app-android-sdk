package com.pax.android.demoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * Created by zcy on 2016/12/2 0002.
 */
public class DownloadParamReceiver extends BroadcastReceiver {
    private static long lastReceiveTime = 0L;
    @Override
    public void onReceive(Context context, Intent intent) {
        // 由于新的PAXSTORE 会发送两种通知，可能会在短时间内收到两种通知，过滤掉其中一种。
        if (System.currentTimeMillis() - lastReceiveTime < 5_000) {
            return;
        }
        lastReceiveTime = System.currentTimeMillis();
        //todo add log to see if the broadcast is received, if not, please check whether the bradcast config is correct
        Log.i("DownloadParamReceiver", "broadcast received");
        //todo receive the broadcast from paxstore, start a service to download parameter files
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //
            context.startForegroundService(new Intent(context, DownloadParamService.class));
        } else {
            context.startService(new Intent(context, DownloadParamService.class));
        }
    }
}
