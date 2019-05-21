package com.pax.android.demoapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pax.market.android.app.sdk.PushConstants.ACTION_DATA_MESSAGE_RECEIVED;
import static com.pax.market.android.app.sdk.PushConstants.ACTION_NOTIFICATION_CLICK;
import static com.pax.market.android.app.sdk.PushConstants.ACTION_NOTIFICATION_MESSAGE_RECEIVED;
import static com.pax.market.android.app.sdk.PushConstants.ACTION_NOTIFY_DATA_MESSAGE_RECEIVED;
import static com.pax.market.android.app.sdk.PushConstants.EXTRA_MESSAGE_CONTENT;
import static com.pax.market.android.app.sdk.PushConstants.EXTRA_MESSAGE_DATA;
import static com.pax.market.android.app.sdk.PushConstants.EXTRA_MESSAGE_NID;
import static com.pax.market.android.app.sdk.PushConstants.EXTRA_MESSAGE_TITLE;

/**
 * Created by fojut on 2019/5/20.
 */
public class PushMessageReceiver extends BroadcastReceiver {

    private static final Logger logger = LoggerFactory.getLogger(PushMessageReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_NOTIFY_DATA_MESSAGE_RECEIVED.equals(intent.getAction())) {
            logger.info("### NOTIFY_DATA_MESSAGE_RECEIVED ###");
            String title = intent.getStringExtra(EXTRA_MESSAGE_TITLE);
            String content = intent.getStringExtra(EXTRA_MESSAGE_CONTENT);
            logger.info("### notification title={}, content={} ###", title, content);
            String dataJson = intent.getStringExtra(EXTRA_MESSAGE_DATA);
            logger.info("### data json={} ###", dataJson);
        } else if (ACTION_DATA_MESSAGE_RECEIVED.equals(intent.getAction())) {
            logger.info("### NOTIFY_DATA_MESSAGE_RECEIVED ###");
            String dataJson = intent.getStringExtra(EXTRA_MESSAGE_DATA);
            logger.info("### data json={} ###", dataJson);
        } else if (ACTION_NOTIFICATION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            logger.info("### NOTIFICATION_MESSAGE_RECEIVED ###");
            String title = intent.getStringExtra(EXTRA_MESSAGE_TITLE);
            String content = intent.getStringExtra(EXTRA_MESSAGE_CONTENT);
            logger.info("### notification title={}, content={} ###", title, content);
        } else if (ACTION_NOTIFICATION_CLICK.equals(intent.getAction())) {
            logger.info("### NOTIFICATION_CLICK ###");
            int nid = intent.getIntExtra(EXTRA_MESSAGE_NID, 0);
            String title = intent.getStringExtra(EXTRA_MESSAGE_TITLE);
            String content = intent.getStringExtra(EXTRA_MESSAGE_CONTENT);
            String dataJson = intent.getStringExtra(EXTRA_MESSAGE_DATA);
            logger.info("### notification nid={}, title={}, content={}, dataJson={} ###", nid, title, content, dataJson);
        }
    }
}
