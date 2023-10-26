/*
 * ******************************************************************************
 * COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *
 *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 * ******************************************************************************
 */

package com.pax.market.android.app.sdk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.media.RingtoneManager;
import android.os.Build;
import android.view.View;
import android.widget.RemoteViews;

import com.pax.market.android.app.sdk.util.PreferencesUtils;
import com.pax.market.api.sdk.java.base.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.pax.market.android.app.sdk.PushConstants.*;

public final class Notifications {

    public static final Notifications I = new Notifications();
    private int nIdSeq = 1;
    private final Map<Integer, Integer> nIds = new HashMap<>();
    private Context context;
    private NotificationManager nm;
    private int smallIcon;
    private Bitmap largeIcon;
    private int defaults;
    private boolean alertOnce;
    private boolean autoCancel;
    private RemoteViews customContentView;
    private RemoteViews customBigContentView;
    public static final String CHANNEL_CLOUD_MSG = "channel_cloud_msg";
    private boolean enabled = true;

    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }
    public boolean getEnabled() {
        return this.enabled;
    }

    public Notifications init(Context context) {
        this.context = context;
        this.nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.smallIcon = R.drawable.ic_notificaiton;
        this.defaults = Notification.DEFAULT_ALL | Notification.FLAG_AUTO_CANCEL;
        this.alertOnce = false;
        this.autoCancel = true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "Cloud message";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_CLOUD_MSG, channelName, importance);
            // 设置默认通知铃声
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null);
            nm.createNotificationChannel(channel);
        }
        return this;
    }

    public boolean hasInit() {
        return context != null;
    }

    /**
     * Set the small icon to use in the notification layouts. Different classes of devices may return different sizes. See the UX guidelines for more information on how to design these icons.
     * Params:
     * icon – A resource ID in the application's package of the drawable to use.
     * @param smallIcon
     * @return
     */
    public Notifications setSmallIcon(int smallIcon) {
        if (context != null) {
            PreferencesUtils.putInt(context, CommonConstants.SP_SMALL_LOGO_ICON, smallIcon);
        }
        this.smallIcon = smallIcon;
        return this;
    }

    /**
     * the icon shows in the nitification content
     * @param largeIcon
     * @return
     */
    public Notifications setLargeIcon(Bitmap largeIcon) {
        this.largeIcon = largeIcon;
        return this;
    }

    /**
     * Set this flag if you would only like the sound, vibrate and ticker to be played if the notification is not already showing.
     * @param alertOnce
     * @return
     */
    public Notifications setOnlyAlertOnce(boolean alertOnce) {
        this.alertOnce = alertOnce;
        return this;
    }

    /**
     * Setting this flag will make it so the notification is automatically canceled when the user clicks it in the panel. The PendingIntent set with setDeleteIntent will be broadcast when the notification is canceled
     * @param autoCancel
     * @return
     */
    public Notifications setAutoCancel(boolean autoCancel) {
        this.autoCancel = autoCancel;
        return this;
    }

    /**
     * Set the default notification options that will be used.
     * The value should be one or more of the following fields combined with bitwise-or: Notification.DEFAULT_SOUND, Notification.DEFAULT_VIBRATE, Notification.DEFAULT_LIGHTS.
     * For all default values, use Notification.DEFAULT_ALL.
     *
     * @param defaults
     * @return
     */
    public Notifications setDefaults(int defaults) {
        this.defaults = defaults;
        return this;
    }

    /**
     * Supply a custom RemoteViews to use instead of the standard one
     * @param customContentView
     * @return
     */
    public Notifications setCustomContentView(RemoteViews customContentView) {
        this.customContentView = customContentView;
        return this;
    }

    /**
     * Supply a custom RemoteViews to use instead of the expanded view
     * @param customBigContentView
     * @return
     */
    public Notifications setCustomBigContentView(RemoteViews customBigContentView) {
        this.customBigContentView = customBigContentView;
        return this;
    }

    public int notify(PushMessage message, String extraData) {
        if (message == null)
            return -1;
        Integer nid = message.getNid();
        //1.if NID not exists, create a new oen that does not exist in nIds
        if (nid == null || nid <= 0) {
            do {
                nid = nIdSeq++;
            } while (nIds.containsKey(nid));
        }

        //handle total count
        Integer count = nIds.get(nid);
        if (count == null) {
            count = 0;
        }
        nIds.put(nid, ++count);
        Intent clickIT = new Intent(ACTION_NOTIFICATION_CLICK);
        clickIT.setPackage(context.getPackageName());
        clickIT.putExtra(EXTRA_MESSAGE_NID, nid);
        clickIT.putExtra(EXTRA_MESSAGE_TITLE, message.getTitle());
        clickIT.putExtra(EXTRA_MESSAGE_CONTENT, message.getContent());
        if (!StringUtils.isEmpty(extraData)) {
            clickIT.putExtra(EXTRA_MESSAGE_DATA, extraData);
        }
        PendingIntent clickPI = PendingIntent.getBroadcast(context, 0, clickIT,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);//处理点击
        Intent cancelIT = new Intent(ACTION_NOTIFICATION_CANCEL);
        cancelIT.putExtra(EXTRA_MESSAGE_NID, nid);
        PendingIntent cancelPI = PendingIntent.getBroadcast(context, 0, cancelIT,
                PendingIntent.FLAG_IMMUTABLE);//处理滑动取消
        nm.notify(nid, build(clickPI, cancelPI, CHANNEL_CLOUD_MSG,
                message.getTitle(),
                message.getTitle(),
                message.getContent(),
                count));
        return nid;
    }

    public void clean(Integer nId) {
        Integer count = nIds.remove(nId);
        if (count != null) nm.cancel(nId);
    }

    public void clean(Intent intent) {
        int nId = intent.getIntExtra(EXTRA_MESSAGE_NID, 0);
        if (nId > 0) clean(nId);
    }

    public void cleanAll() {
        nIds.clear();
        nm.cancelAll();
    }

    private Notification build(PendingIntent clickIntent, PendingIntent cancelIntent, String channelId,
                               String ticker, String title, String content, int number) {

        long when = System.currentTimeMillis();
        Notification.Builder mBuilder;
        if (Build.VERSION.SDK_INT > 30) {
            mBuilder = new Notification.Builder(context, channelId);
            mBuilder.setStyle(new Notification.DecoratedCustomViewStyle());
            mBuilder.setCustomContentView(customContentView == null ? getNotificationContentView(
                    R.layout.view_notificaiton_small, title, content, when) : customContentView);
            mBuilder.setCustomBigContentView(customBigContentView == null ? getNotificationContentView(
                    R.layout.view_notificaiton_no_icon, title, content, when) : customBigContentView);
        } else if (Build.VERSION.SDK_INT >= 26) {
            mBuilder = new Notification.Builder(context, channelId);
            mBuilder.setCustomContentView(customContentView == null ? getDefaultContentView(title, content, when) : customContentView);
        } else {
            mBuilder = new Notification.Builder(context);
            mBuilder.setContent(customContentView == null ? getDefaultContentView(title, content, when) : customContentView)
                    .setDefaults(defaults);
        }
        return mBuilder.setSmallIcon(smallIcon)
                .setTicker(ticker)
                .setContentIntent(clickIntent)
                .setDeleteIntent(cancelIntent)
                .setAutoCancel(autoCancel)
                .setOnlyAlertOnce(alertOnce)
                .setWhen(when)
                .setNumber(number)
                .build();
    }

    /**
     * 适配安卓12的展开和折叠两种视图
     * @param viewId view Id
     * @param title title
     * @param content content
     * @param when time
     * @return
     */
    private RemoteViews getNotificationContentView(int viewId, String title, String content, long when) {
        RemoteViews defaultContentView = new RemoteViews(context.getPackageName(), viewId);
        defaultContentView.setTextViewText(R.id.tv_title, title);
        defaultContentView.setTextViewText(R.id.tv_content, content);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault()) ;
        defaultContentView.setTextViewText(R.id.tv_time, df.format(new Date(when)));
        return defaultContentView;
    }

    private RemoteViews getDefaultContentView(String title, String content, long when) {
        RemoteViews defaultContentView = new RemoteViews(context.getPackageName(), R.layout.view_notificaiton);
        //set img id for the image resource
        if (largeIcon != null) {
            defaultContentView.setImageViewBitmap(R.id.iv_icon, largeIcon);
            defaultContentView.setViewVisibility(R.id.iv_icon, View.VISIBLE);
        } else {
            defaultContentView.setViewVisibility(R.id.iv_icon, View.GONE);
        }
        defaultContentView.setTextViewText(R.id.tv_title, title);
        defaultContentView.setTextViewText(R.id.tv_content, content);
        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault()) ;
        defaultContentView.setTextViewText(R.id.tv_time, df.format(new Date(when)));
        return defaultContentView;
    }
}