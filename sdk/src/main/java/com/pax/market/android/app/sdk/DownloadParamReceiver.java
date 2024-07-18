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

    private static final String STORE_PACKAGENAME = "com.pax.market.android.app";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ((intent != null && intent.getLongExtra(ParamService.TERMINAL_SEND_TIME, -1L) > 0)
                || getVerCodeByPackageName(context, STORE_PACKAGENAME) >= 200) {
            Log.w("DownloadParamReceiver", "Ignore this broadcast, since STORE client will send Intent to ParamService");
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
     * getVersionCode by packageName
     *
     * @param packageName
     * @return
     */
    public static int getVerCodeByPackageName(Context context, String packageName) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager()
                    .getPackageInfo(packageName, 0).versionCode;
        } catch (Exception e) {
            Log.e("DownloadParamReceiver", e.toString());
        }
        return verCode;
    }
}
