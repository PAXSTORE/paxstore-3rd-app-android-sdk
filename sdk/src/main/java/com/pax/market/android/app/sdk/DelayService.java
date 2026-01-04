package com.pax.market.android.app.sdk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.pax.market.android.app.sdk.util.NotificationUtils;

public class DelayService extends Service {


    /**
     * Ignore tasks within 5 seconds
     */
    private static final long TIME_FILTER = 5_000L;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, DelayService.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    Handler handler ;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
            NotificationUtils.showForeGround(this, "Delay Service", ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE);
        } else {
            NotificationUtils.showForeGround(this, "Delay Service", 0);
        }
        Log.d("DelayService", "delayService onStartCommand");
        if (handler == null) {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent startIntent = new Intent(CommonConstants.ACTION_START_CUSTOMER_SERVICE);
                    startIntent.setPackage(getPackageName());
                    startIntent.addCategory(getPackageName());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(startIntent);
                    } else {
                        startService(startIntent);
                    }
                    stopSelf();
                }
            }, TIME_FILTER);
        } else {
            Log.w("DelayService", "cmd too fast, ignore");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler = null;
    }
}
