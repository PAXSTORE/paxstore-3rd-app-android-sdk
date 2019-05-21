package com.pax.market.android.app.sdk;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pax.market.android.app.sdk.dto.StoreProxyInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * Created by fojut on 2019/1/11.
 */
public class ProxyChangeService extends IntentService {

    private static final String TAG = ProxyChangeService.class.getSimpleName();
    private static final Logger logger = LoggerFactory.getLogger(ProxyChangeService.class);

    private Gson gson;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ProxyChangeService(String name) {
        super(name);
    }

    public ProxyChangeService() {
        super(TAG);
    }

    public static Intent getCallingIntent(Context context, byte[] extra) {
        return new Intent(context, ProxyChangeService.class)
                .putExtra(ProxyChangeReceiver.EXTRA_PROXY_INFO, extra);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.gson = new GsonBuilder().create();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        byte[] proxyInfoBytes = intent.getByteArrayExtra(ProxyChangeReceiver.EXTRA_PROXY_INFO);
        if(proxyInfoBytes != null){
            StoreProxyInfo storeProxyInfo = gson.fromJson(new String(proxyInfoBytes, Charset.defaultCharset()), StoreProxyInfo.class);
            if(storeProxyInfo != null) {
                logger.info(">>> Receive proxy change broadcast: proxy[@{}/{}:{}], has proxy authenticator={}",
                        storeProxyInfo.getType() == 1 ? "HTTP" : storeProxyInfo.getType() == 2 ? "SOCKS" : "DIRECT",
                        storeProxyInfo.getHost(), storeProxyInfo.getPort(), storeProxyInfo.getAuthorization() != null);
                StoreSdk.getInstance().updateStoreProxyInfo(getApplicationContext(), storeProxyInfo);
            } else {
                logger.warn(">>> Receive proxy change broadcast with proxy[NULL], just ignore...");
            }
        } else {
            logger.warn(">>> Receive proxy change broadcast with NULL intent extra, just ignore...");
        }
    }
}
