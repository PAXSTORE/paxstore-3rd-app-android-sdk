package com.pax.market.android.app.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pax.market.android.app.sdk.dto.StoreProxyInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fojut on 2019/1/10.
 */
public class ProxyChangeReceiver extends BroadcastReceiver {

    private static final Logger logger = LoggerFactory.getLogger(ProxyChangeReceiver.class);

    public static final String ACTION_PROXY_CHANGE = "com.paxstore.PROXY_CHANGE";
    public static final String EXTRA_PROXY_INFO = "intent.extra.STORE_PROXY_INFO";

    private Gson gson = new GsonBuilder().create();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(ACTION_PROXY_CHANGE.equals(action)){
            StoreProxyInfo storeProxyInfo = gson.fromJson(intent.getStringExtra(EXTRA_PROXY_INFO), StoreProxyInfo.class);
            if(storeProxyInfo != null){
                logger.info(">>> Receive proxy change broadcast: proxy[@{}/{}:{}], has proxy authenticator={}",
                        storeProxyInfo.getType() == 1 ? "HTTP" : storeProxyInfo.getType() == 2 ? "SOCKS" : "DIRECT",
                        storeProxyInfo.getHost(), storeProxyInfo.getPort(), storeProxyInfo.getAuthorization() != null);
                StoreSdk.getInstance().updateStoreProxyInfo(context, storeProxyInfo);
            } else {
                logger.warn(">>> Receive proxy change broadcast with proxy[NULL], just ignore...");
            }
        }
    }
}
