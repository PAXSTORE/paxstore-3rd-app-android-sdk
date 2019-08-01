package com.pax.market.android.app.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by fojut on 2019/1/10.
 */
public class ProxyChangeReceiver extends BroadcastReceiver {

    public static final String ACTION_PROXY_CHANGE = "com.paxstore.PROXY_CHANGE";
    public static final String EXTRA_PROXY_INFO = "intent.extra.STORE_PROXY_INFO";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(ACTION_PROXY_CHANGE.equals(action)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //
                context.startForegroundService(ProxyChangeService.getCallingIntent(context, intent.getByteArrayExtra(EXTRA_PROXY_INFO)));
            } else {
                context.startService(ProxyChangeService.getCallingIntent(context, intent.getByteArrayExtra(EXTRA_PROXY_INFO)));
            }
        }
    }
}
