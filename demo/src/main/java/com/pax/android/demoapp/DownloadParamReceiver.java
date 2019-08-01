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
    @Override
    public void onReceive(Context context, Intent intent) {
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
