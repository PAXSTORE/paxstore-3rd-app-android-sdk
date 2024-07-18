package com.pax.market.android.app.sdk;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.pax.market.android.app.sdk.util.NotificationUtils;

/**
 * Created by zhangcy on 2020/5/20.
 */
public class ParamService extends IntentService {
    private static final String TAG = ParamService.class.getSimpleName();
    public static final String TERMINAL_SERIALNUM = "SN";
    public static final String TERMINAL_SEND_TIME = "SEND_TIME";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ParamService(String name) {
        super(name);
    }

    public ParamService() {
        super(TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationUtils.showForeGround(this, "Param Service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        String sn = (String) intent.getSerializableExtra(TERMINAL_SERIALNUM);

        if (sn == null) {
            Log.w(TAG, "sn == null");
            return;
        }
        // STORE client versionCode>=200
        Log.w("ParamService", "intent received");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(DelayService.getCallingIntent(getApplicationContext()));
        } else {
            startService(DelayService.getCallingIntent(getApplicationContext()));
        }
    }
}
