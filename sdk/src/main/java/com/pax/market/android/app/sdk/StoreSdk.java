package com.pax.market.android.app.sdk;

import android.content.Context;

import com.pax.market.android.app.sdk.dto.StoreProxyInfo;
import com.pax.market.api.sdk.java.api.param.ParamApi;
import com.pax.market.api.sdk.java.api.sync.SyncApi;
import com.pax.market.api.sdk.java.base.client.ProxyDelegate;
import com.pax.market.api.sdk.java.base.exception.NotInitException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangchenyang on 2018/5/23.
 */

public class StoreSdk {
    private static final Logger logger = LoggerFactory.getLogger(StoreSdk.class);

    private static final String TAG = "StoreSdk";
    private static volatile StoreSdk instance;

    private ParamApi paramApi;
    private SyncApi syncApi;
    private Semaphore semaphore;

    private String appKey;
    private String appSecret;


    public StoreSdk() {
        semaphore = new Semaphore(2);
    }

    public static StoreSdk getInstance() {
        if (instance == null) {
            synchronized (StoreSdk.class) {
                if (instance == null) {
                    instance = new StoreSdk();
                }
            }
        }
        return instance;
    }

    /**
     * Init StoreSdk
     *
     * @param context
     * @param appKey
     * @param appSecret
     * @param terminalSerialNo
     * @param callback
     */
    public void init(final Context context, final String appKey, final String appSecret,
                     final String terminalSerialNo, final BaseApiService.Callback callback) throws NullPointerException {
        if (paramApi == null && syncApi == null && semaphore.availablePermits() != 1) {
            validParams(context, appKey, appSecret, terminalSerialNo);
            this.appKey = appKey;
            this.appSecret = appSecret;
            try {
                logger.debug(TAG, "init acquire 1");
                semaphore.acquire(1);
            } catch (InterruptedException e) {
                logger.error(TAG, "e:" + e);
            }
            BaseApiService.getInstance(context).init(appKey, appSecret, terminalSerialNo, callback,
                    new BaseApiService.ApiCallBack() {

                        @Override
                        public void initSuccess(String baseUrl) {
                            initApi(baseUrl, appKey, appSecret, terminalSerialNo, BaseApiService.getInstance(context));
                            semaphore.release(1);
                            logger.debug(TAG, "initSuccess >> release acquire 1");
                        }

                        @Override
                        public void initFailed() {
                            semaphore.release(1);
                            logger.debug(TAG, "initFailed >> release acquire 1");
                        }
                    });
        } else {
            logger.debug(TAG, "Initialization is on process or has been done");
        }
    }

    /**
     * Get ParamApi instance
     *
     * @return
     * @throws NotInitException
     */
    public ParamApi paramApi() throws NotInitException {
        if (paramApi == null) {
            acquireSemaphore();
            if (paramApi == null) {
                throw new NotInitException("Not initialized");
            }
        }
        return paramApi;
    }


    /**
     * Get SyncApi instance
     *
     * @return
     * @throws NotInitException
     */
    public SyncApi syncApi() throws NotInitException {
        if (syncApi == null) {
            acquireSemaphore();
            if (syncApi == null) {
                throw new NotInitException("Not initialized");
            }
        }
        return syncApi;
    }


    /**
     * Check if initialized
     * true: initialized
     * @return
     */
    public boolean checkInitialization() {
        if (paramApi != null && syncApi != null) {
            return true;
        }
        return false;
    }

    /**
     * Make sure StoreSdk is not initailizing now.
     * <p>
     * Since developer will call {@link #paramApi() } or {@link  #syncApi()}
     * when doing {@link #init}(which will take 1 to 2 seconds to finish),
     * at these period, any StoreSdk api call will fail.
     * So we add these method to hold the api call, until {@link #init} get
     * a result or timeout after 5 seconds.
     */
    private void acquireSemaphore() {
        try {
            logger.debug(TAG, "acquireSemaphore api try acquire 2");
            Long startTime = System.currentTimeMillis();
            semaphore.tryAcquire(2, 5, TimeUnit.SECONDS);
            logger.debug(TAG, "tryAcquire cost Time:" + (System.currentTimeMillis() - startTime));
        } catch (InterruptedException e) {
            logger.error(TAG, "e:" + e);
        }
        if (semaphore.availablePermits() == 0) {
            semaphore.release(2);
            logger.debug(TAG, "acquireSemaphore api release acquire 2");
        }
    }

    /**
     * Update inquirer: Store app will ask you before installing the
     * new version of your app.
     * Ignore this if you don't have Update inquirer requirement.
     * <p>
     * You can implement {@link com.pax.market.android.app.sdk.StoreSdk.Inquirer#isReadyUpdate()}
     * to tell Store App whether your app can be updated now.
     *
     * @param inquirer
     */
    public void initInquirer(final Inquirer inquirer) {
        RPCService.initInquirer(appKey, appSecret, new RPCService.Inquirer() {
            @Override
            public boolean isReadyUpdate() {
                return inquirer.isReadyUpdate();
            }
        });
    }

    /**
     * Context, appKey， appSecret， terminalSerialNo will be validated,
     * NullPointerException will be throw when any of this is null.
     *
     * @param context
     * @param appKey
     * @param appSecret
     * @param terminalSerialNo
     * @throws NullPointerException
     */
    private void validParams(Context context, String appKey, String appSecret, String terminalSerialNo) throws NullPointerException {
        if (context == null) {
            throw new NullPointerException("Context needed");
        }
        if (appKey == null || appKey.isEmpty()) {
            throw new NullPointerException("AppKey needed");
        }
        if (appSecret == null || appSecret.isEmpty()) {
            throw new NullPointerException("AppSecret needed");
        }
        if (terminalSerialNo == null || terminalSerialNo.isEmpty()) {
            throw new NullPointerException("TerminalSerialNo needed");
        }
    }

    /**
     * If you known the exact apiUrl, you can call this method to
     * init ParamApi and SyncApi directly instead of calling method {@link #init};
     *
     * @param apiUrl
     * @param appKey
     * @param appSecret
     * @param terminalSerialNo
     */
    public void initApi(String apiUrl, String appKey, String appSecret, String terminalSerialNo, ProxyDelegate proxyDelegate) {
        paramApi = new ParamApi(apiUrl, appKey, appSecret, terminalSerialNo).setProxyDelegate(proxyDelegate);
        syncApi = new SyncApi(apiUrl, appKey, appSecret, terminalSerialNo).setProxyDelegate(proxyDelegate);
    }

    /**
     *  callback of update inquirer {@link #initInquirer}
     *  this method will tell store app that whether your app can be updated.
     */
    public interface Inquirer {
        boolean isReadyUpdate();
    }

    /**
     * To retrieve the base terminal info from PAXSTORE Client.
     * Required: PAXSTORE client version 6.1 and above
     * @param context
     * @param callback refer to BaseApiService.ICallBack, you need to handle onSuccess and OnError method. when onSuccess, will return a TerminalInfo DTO as result.
     *         e.g
     *         new BaseApiService.Callback() {
     *              //@Override
     *              public void onSuccess(Object obj) {
     *                  TerminalInfo terminalInfo = (TerminalInfo) obj;
     *                  Log.i("onSuccess: ",terminalInfo.toString());
     *              }
     *
     *              //@Override
     *              public void onError(Exception e) {
     *                  Log.i("onError: ",e.toString());
     *              }
     *         }
     * For the return Object TerminalInfo, please refer to com.pax.market.android.app.sdk.dto.TerminalInfo
     *
     */
    public void getBaseTerminalInfo(Context context, BaseApiService.ICallBack callback) {
        BaseApiService.getInstance(context).getBaseTerminalInfo(callback);
    }

    /**
     * Sync and update PAXSTORE proxy information
     * @param context
     * @param storeProxyInfo
     */
    public void updateStoreProxyInfo(Context context, StoreProxyInfo storeProxyInfo){
        BaseApiService.getInstance(context).setStoreProxyInfo(storeProxyInfo);
        paramApi.setProxyDelegate(BaseApiService.getInstance(context));
        syncApi.setProxyDelegate(BaseApiService.getInstance(context));
    }
}
