package com.pax.market.android.app.sdk.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import androidx.core.app.NotificationCompat;

import com.pax.market.android.app.sdk.BuildConfig;
import com.pax.market.android.app.sdk.R;

public class NotificationUtils {
    private static final int NOTIFICATION_ID = 1002;

    public static void showForeGround(Service service, String content) {
        NotificationChannel mChannel = null;
        NotificationManager notificationManager = (NotificationManager) service.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(BuildConfig.APPLICATION_ID, service.getApplicationContext().getString(R.string.app_name), NotificationManager.IMPORTANCE_NONE);
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
            notificationManager.createNotificationChannel(mChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(service.getApplicationContext(), BuildConfig.APPLICATION_ID);
            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setContentText(content);
            builder.setAutoCancel(true);
            builder.setShowWhen(true);

            // 这里两个通知使用同一个id且必须按照这个顺序后调用startForeground
            service.startForeground(NOTIFICATION_ID, builder.build());
        }
    }
}
