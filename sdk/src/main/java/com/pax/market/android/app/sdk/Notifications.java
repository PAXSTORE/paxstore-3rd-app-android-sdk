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
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

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
    private RemoteViews customContentView;
    public static final String CHANNEL_CLOUD_MSG = "channel_cloud_msg";

    public Notifications init(Context context) {
        this.context = context;
        this.nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.smallIcon = R.drawable.ic_notificaiton;
        this.defaults = Notification.DEFAULT_ALL | Notification.FLAG_AUTO_CANCEL;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "Cloud message";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_CLOUD_MSG, channelName, importance);
            nm.createNotificationChannel(channel);
        }
        return this;
    }

    public boolean hasInit() {
        return context != null;
    }

    public Notifications setSmallIcon(int smallIcon) {
        this.smallIcon = smallIcon;
        return this;
    }

    public Notifications setLargeIcon(Bitmap largeIcon) {
        this.largeIcon = largeIcon;
        return this;
    }

    public Notifications setDefaults(int defaults) {
        this.defaults = defaults;
        return this;
    }

    public Notifications setCustomContentView(RemoteViews customContentView) {
        this.customContentView = customContentView;
        return this;
    }

    public int notify(PushMessage message, String extraData) {
        if (message == null)
            return -1;
        Integer nid = message.getNid();
        //1.如果NID不存在则新生成一个，且新生成的Id在nIds是不存在的
        if (nid == null || nid <= 0) {
            do {
                nid = nIdSeq++;
            } while (nIds.containsKey(nid));
        }

        //处理总数
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
        PendingIntent clickPI = PendingIntent.getBroadcast(context, 0, clickIT, PendingIntent.FLAG_UPDATE_CURRENT);//处理点击
        Intent cancelIT = new Intent(ACTION_NOTIFICATION_CANCEL);
        cancelIT.putExtra(EXTRA_MESSAGE_NID, nid);
        PendingIntent cancelPI = PendingIntent.getBroadcast(context, 0, cancelIT, 0);//处理滑动取消
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

        return new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(smallIcon)
                .setContent(customContentView == null ? getDefaultContentView(title, content, when) : customContentView)
                .setTicker(ticker)
                .setContentIntent(clickIntent)
                .setDeleteIntent(cancelIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setDefaults(defaults)
                .setWhen(when)
                .setNumber(number)
                .build();
    }

    private RemoteViews getDefaultContentView(String title, String content, long when) {
        RemoteViews defaultContentView = new RemoteViews(context.getPackageName(), R.layout.view_notificaiton);
        //设置对应IMAGEVIEW的ID的资源图片
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