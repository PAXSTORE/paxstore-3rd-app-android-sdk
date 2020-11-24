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
        if ((intent != null && intent.getLongExtra(ParamService.TERMINAL_SEND_TIME, -1L) > 0)) {
            //1.如果是有sendTime的，证明是>=7.4.0的client， 肯定会有service通知，不需要receiver处理。
            //2.如果无sendTime, 那么就需要判断是否重复，可能PAXSTORE client会通知一遍，
            // 自己的service会通知一遍，因为没有sendTime，所以无法判断client那边会用哪种方式通知过来
            return;
        }

        Log.i("DownloadParamReceiver", "broadcast received");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(DelayService.getCallingIntent(context));
        } else {
            context.startService(DelayService.getCallingIntent(context));
        }
    }
}
