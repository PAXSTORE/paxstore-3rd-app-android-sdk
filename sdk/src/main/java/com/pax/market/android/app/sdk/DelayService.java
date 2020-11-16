package com.pax.market.android.app.sdk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;

import com.pax.market.android.app.sdk.util.NotificationUtils;

import static com.pax.market.android.app.sdk.DownloadParamReceiver.TIME_FILTER;

public class DelayService extends Service {

    private static String ACTION_START_CUSTOMER_SERVICE = "com.sdk.service.ACTION_TO_DOWNLOAD_PARAMS";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Intent getCallingIntent(Context context){
        return new Intent(context, DelayService.class);
    }

    Handler handler = new Handler();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationUtils.showForeGround(this, "Delay Service");
        Log.d("DelayService", "delayService onStartCommand");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent startIntent = new Intent(ACTION_START_CUSTOMER_SERVICE);
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
        return super.onStartCommand(intent, flags, startId);
    }
}
