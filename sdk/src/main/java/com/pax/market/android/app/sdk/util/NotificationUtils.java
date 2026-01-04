package com.pax.market.android.app.sdk.util;

import static com.pax.market.android.app.sdk.CommonConstants.SP_SMALL_LOGO_ICON;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.Build;

import com.pax.market.android.app.sdk.R;

public class NotificationUtils {
    private static final int NOTIFICATION_ID = 1002;

    private static final String NOTIFICATION_CHANNEL_ID = "com.pax.market.android.app.sdk";

    /**
     * @deprecated This method is deprecated because it does not support the mandatory
     * foreground service types required by Android 14 (API 34).
     * <b>Migration Guide:</b><br>
     * Please migrate to {@link #showForeGround(Service, int, String, int)} to explicitly
     * provide the correct service type for your business logic.
     */
    public static void showForeGround(Service service, int smallIcon, String content) {
        showForeGround(service, smallIcon, content, 0);
    }

    /**
     * @deprecated This method is deprecated because it does not support the mandatory
     * foreground service types required by Android 14 (API 34).
     * <b>Migration Guide:</b><br>
     * Please migrate to {@link #showForeGround(Service, String, int)} to explicitly
     * provide the correct service type for your business logic.
     */
    @Deprecated
    public static void showForeGround(Service service, String content) {
        showForeGround(service, content, 0);
    }


    /**
     * Updated method to support Android 14 foreground service types.
     *
     * @param service               The Service instance.
     * @param content               Content to display in the notification.
     * @param foregroundServiceType The type(s) of foreground service,
     *                              e.g., {@link ServiceInfo#FOREGROUND_SERVICE_TYPE_DATA_SYNC}.
     */
    public static void showForeGround(Service service, String content, int foregroundServiceType) {
        showForeGround(service, PreferencesUtils.getInt(service, SP_SMALL_LOGO_ICON, R.drawable.ic_notificaiton), content, foregroundServiceType);

    }


    /**
     * Updated method to support Android 14 foreground service types.
     *
     * @param service               The Service instance.
     * @param content               Content to display in the notification.
     * @param smallIcon             The icon display in the notification.
     * @param foregroundServiceType The type(s) of foreground service,
     *                              e.g., {@link ServiceInfo#FOREGROUND_SERVICE_TYPE_DATA_SYNC}.
     */
    public static void showForeGround(Service service, int smallIcon, String content, int foregroundServiceType) {

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34
                service.startForeground(NOTIFICATION_ID, builder.build(), foregroundServiceType);
            } else {
                service.startForeground(NOTIFICATION_ID, builder.build());
            }
        }
    }


}
