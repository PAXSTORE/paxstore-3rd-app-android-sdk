package com.pax.android.demoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by zcy on 2016/12/2 0002.
 */
public class DownloadParamReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DownloadParamReceiver", "broadcast received");
        //receive the broadcast from paxstore, start a service to download parameter files
        context.startService(new Intent(context, DownloadParamService.class));
    }
}
