package com.pax.market.android.app.sdk;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.pax.market.android.app.sdk.util.NotificationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Jwts;

/**
 * Created by fojut on 2017/8/16.
 */
public class RPCService extends Service {

    private static final Logger logger = LoggerFactory.getLogger(RPCService.class);
    private static final int MSG_SERVICE_CONNECTED = 0x110;
    private static final int MSG_VERIFY_AUTH = 0x111;
    private static final int MSG_CONFIRM_UPDATE = 0x112;
    private static final int MSG_CONFIRM_UPDATE_RESULT = 0x113;
    private static final String BUNDLE_AUTH_INFO = "AUTH_INFO";
    private static final String BUNDLE_READY_UPDATE = "READY_UPDATE";
    private static Inquirer inquirer;
    private static String mAppKey;
    private static String mAppSecret;
    private Messenger messenger = new Messenger(new MessengerHandler());

    public static void initInquirer(String appKey, String appSecret, Inquirer inquirer) {
        mAppKey = appKey;
        mAppSecret = appSecret;
        RPCService.inquirer = inquirer;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }


    private String generateToken(String appKey, String appSecret) {
        byte[] secretBytes = appSecret.getBytes();

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            Log.e("RPCService", "generateToken NoSuchAlgorithmException: " + e);
            return null;
        }
        byte[] hashedBytes = digest.digest(secretBytes);

        SecretKey secretKey = new SecretKeySpec(hashedBytes, 0, hashedBytes.length, "HmacSHA512");
        String token = Jwts.builder()
                .subject(appKey)
                .expiration(generateExpirationDate())
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
        return token;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationUtils.showForeGround(this, "Install Inquirer");
        return super.onStartCommand(intent, flags, startId);
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + 5 * 60 * 1000);
    }

    private Bundle readyUpdate() {
        Bundle bundle = new Bundle();
        if (inquirer != null) {
            bundle.putString(BUNDLE_AUTH_INFO, generateToken(mAppKey, mAppSecret));
            bundle.putBoolean(BUNDLE_READY_UPDATE, inquirer.isReadyUpdate());
        } else {
            logger.warn(">>> inquirer not initialized, just return true...");
            bundle.putBoolean(BUNDLE_READY_UPDATE, true);
        }
        return bundle;
    }

    public interface Inquirer {
        boolean isReadyUpdate();
    }

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONFIRM_UPDATE: {
                    logger.debug(">>> receive confirm update message...");
                    Messenger messenger = msg.replyTo;
                    Message message = Message.obtain(null, MSG_CONFIRM_UPDATE_RESULT);
                    message.setData(readyUpdate());
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        logger.error(">>> send message error.", e);
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopSelf();
        return super.onUnbind(intent);
    }
}
