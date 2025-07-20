package com.pax.market.android.app.sdk.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;

import com.pax.market.android.app.sdk.R;

import static com.pax.market.android.app.sdk.CommonConstants.SP_SMALL_LOGO_ICON;

public class NotificationUtils {
    private static final int NOTIFICATION_ID = 1002;

    private static final String NOTIFICATION_CHANNEL_ID = "com.pax.market.android.app.sdk";
    /**
     *
     * @param service
     * @param smallIcon
     * @param content
     */
    public static void showForeGround(Service service, int smallIcon, String content) {
        NotificationChannel mChannel = null;
        NotificationManager notificationManager = (NotificationManager) service.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, service.getApplicationContext().getString(R.string.app_name), NotificationManager.IMPORTANCE_NONE);
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
            notificationManager.createNotificationChannel(mChannel);

            Notification.Builder builder = new Notification.Builder(service.getApplicationContext(), NOTIFICATION_CHANNEL_ID);
            builder.setContentText(content);
            builder.setSmallIcon(smallIcon);
            builder.setAutoCancel(true);
            builder.setShowWhen(true);

            // 这里两个通知使用同一个id且必须按照这个顺序后调用startForeground
            service.startForeground(NOTIFICATION_ID, builder.build());
        }
    }

    /**
     *
     * @param service
     * @param content
     */
    public static void showForeGround(Service service, String content) {
        NotificationChannel mChannel = null;
        NotificationManager notificationManager = (NotificationManager) service.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, service.getApplicationContext().getString(R.string.app_name), NotificationManager.IMPORTANCE_NONE);
            mChannel.setSound(null, null);
            mChannel.enableVibration(false);
            notificationManager.createNotificationChannel(mChannel);

            Notification.Builder builder = new Notification.Builder(service.getApplicationContext(), NOTIFICATION_CHANNEL_ID);
            builder.setContentText(content);
            builder.setSmallIcon(PreferencesUtils.getInt(service, SP_SMALL_LOGO_ICON, R.drawable.ic_notificaiton));
            builder.setAutoCancel(true);
            builder.setShowWhen(true);

            // 这里两个通知使用同一个id且必须按照这个顺序后调用startForeground
            service.startForeground(NOTIFICATION_ID, builder.build());
        }
    }
}
