package com.pax.market.android.app.sdk;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.pax.market.android.app.sdk.dto.LocationInfo;
import com.pax.market.android.app.sdk.dto.OnlineStatusInfo;
import com.pax.market.android.app.sdk.dto.QueryResult;
import com.pax.market.android.app.sdk.dto.StoreProxyInfo;
import com.pax.market.api.sdk.java.api.param.ParamApi;
import com.pax.market.api.sdk.java.api.sync.GoInsightApi;
import com.pax.market.api.sdk.java.api.sync.SyncApi;
import com.pax.market.api.sdk.java.api.update.UpdateApi;
import com.pax.market.api.sdk.java.base.client.ProxyDelegate;
import com.pax.market.api.sdk.java.base.exception.NotInitException;
import com.pax.market.api.sdk.java.base.util.CryptoUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangchenyang on 2018/5/23.
 */

public class StoreSdk {
    private static final Logger logger = LoggerFactory.getLogger(StoreSdk.class);

    private static final String PAXSTORE_PACKAGENAME = "com.pax.market.android.app";
    private static final String PAXSTORE_DETAIL_PAGE = "com.pax.market.android.app.presentation.search.view.activity.SearchAppDetailActivity";
    private static final String PAXSTORE_DOWNLOADLIST_PAGE = "com.pax.market.android.app.presentation.downloadlist.view.activity.DownloadListActivity";

    private static final String URI_PREFIX = "market://detail?id=%s";
    private static volatile StoreSdk instance;
    private ParamApi paramApi;
    private SyncApi syncApi;
    private GoInsightApi goInsightApi;
    private UpdateApi updateApi;
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
        if (paramApi == null && syncApi == null && updateApi == null && semaphore.availablePermits() != 1) {
            validParams(context, appKey, appSecret, terminalSerialNo);
            this.appKey = appKey;
            this.appSecret = appSecret;
            try {
                logger.debug("init acquire 1");
                semaphore.acquire(1);
            } catch (InterruptedException e) {
                logger.error("e:" + e);
            }
            BaseApiService.getInstance(context).init(appKey, appSecret, terminalSerialNo, callback,
                    new BaseApiService.ApiCallBack() {

                        @Override
                        public void initSuccess(String baseUrl) {
                            initApi(baseUrl, appKey, appSecret, terminalSerialNo, BaseApiService.getInstance(context));
                            semaphore.release(1);
                            logger.debug("initSuccess >> release acquire 1");
                        }

                        @Override
                        public void initFailed() {
                            semaphore.release(1);
                            logger.error("initFailed >> release acquire 1");
                        }
                    });
        } else {
            logger.debug("Initialization is on process or has been done");
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

    public GoInsightApi goInsightApi() throws NotInitException {
        if (goInsightApi == null) {
            acquireSemaphore();
            if (goInsightApi == null) {
                throw new NotInitException("Not initialized");
            }
        }
        return goInsightApi;
    }

    /**
     * Get UpdateApi instance
     *
     * @return
     * @throws NotInitException
     */
    public UpdateApi updateApi() throws NotInitException {
        if (updateApi == null) {
            acquireSemaphore();
            if (updateApi == null) {
                throw new NotInitException("Not initialized");
            }
        }
        return updateApi;
    }

    /**
     * Check if initialized
     * true: initialized
     *
     * @return
     */
    public boolean checkInitialization() {
        if (paramApi != null && syncApi != null && updateApi != null) {
            return true;
        }
        return false;
    }

    /**
     * Make sure StoreSdk is not initailizing now.
     * <p>
     * Since developer will call {@link #paramApi() } or {@link  #syncApi()}
     * of {@link #updateApi() } or {@link  #goInsightApi()}
     * when doing {@link #init}(which will take 1 to 2 seconds to finish),
     * at these period, any StoreSdk api call will fail.
     * So we add these method to hold the api call, until {@link #init} get
     * a result or timeout after 5 seconds.
     */
    private void acquireSemaphore() {
        try {
            logger.debug("acquireSemaphore api try acquire 2");
            Long startTime = System.currentTimeMillis();
            semaphore.tryAcquire(2, 5, TimeUnit.SECONDS);
            logger.debug("tryAcquire cost Time:" + (System.currentTimeMillis() - startTime));
        } catch (InterruptedException e) {
            logger.error("e:" + e);
        }
        if (semaphore.availablePermits() == 0) {
            semaphore.release(2);
            logger.debug("acquireSemaphore api release acquire 2");
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
        updateApi = new UpdateApi(apiUrl, appKey, appSecret, terminalSerialNo).setProxyDelegate(proxyDelegate);
        goInsightApi = new GoInsightApi(apiUrl, appKey, appSecret, terminalSerialNo, TimeZone.getDefault()).setProxyDelegate(proxyDelegate);
    }

    /**
     * To retrieve the base terminal info from PAXSTORE Client.
     * Required: PAXSTORE client version 6.1 and above
     *
     * @param context
     * @param callback refer to BaseApiService.ICallBack, you need to handle onSuccess and OnError method. when onSuccess, will return a TerminalInfo DTO as result.
     *                 e.g
     *                 new BaseApiService.Callback() {
     *                 //@Override
     *                 public void onSuccess(Object obj) {
     *                 TerminalInfo terminalInfo = (TerminalInfo) obj;
     *                 Log.i("onSuccess: ",terminalInfo.toString());
     *                 }
     *                 <p>
     *                 //@Override
     *                 public void onError(Exception e) {
     *                 Log.i("onError: ",e.toString());
     *                 }
     *                 }
     *                 For the return Object TerminalInfo, please refer to com.pax.market.android.app.sdk.dto.TerminalInfo
     */
    public void getBaseTerminalInfo(Context context, BaseApiService.ICallBack callback) {
        BaseApiService.getInstance(context).getBaseTerminalInfo(callback);
    }

    /**
     * Sync and update PAXSTORE proxy information
     *
     * @param context
     * @param storeProxyInfo
     */
    public void updateStoreProxyInfo(Context context, StoreProxyInfo storeProxyInfo) {
        BaseApiService.getInstance(context).setStoreProxyInfo(storeProxyInfo);
        if (paramApi != null) {
            paramApi.setProxyDelegate(BaseApiService.getInstance(context));
        } else {
            logger.warn("ParamApi is not initialized, please init StoreSdk first...");
        }
        if (syncApi != null) {
            syncApi.setProxyDelegate(BaseApiService.getInstance(context));
        } else {
            logger.warn("SyncApi is not initialized, please init StoreSdk first...");
        }
        if (updateApi != null) {
            updateApi.setProxyDelegate(BaseApiService.getInstance(context));
        } else {
            logger.warn("UpdateApi is not initialized, please init StoreSdk first...");
        }
    }

    public String aesDecrypt(String encryptedData) {
        if (appSecret == null) {
            logger.error("Store sdk not initialized");
        }
        return  CryptoUtils.aesDecrypt(encryptedData, appSecret);
    }


    public void openAppDetailPage(String packageName, Context context) {
        String url = String.format(URI_PREFIX, packageName);
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setClassName(PAXSTORE_PACKAGENAME, PAXSTORE_DETAIL_PAGE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * open PAXSTORE's download page
     * @param packageName your app packagename
     * @param context
     */
    public void openDownloadListPage(String packageName, Context context) {
        String url = String.format(URI_PREFIX, packageName);
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setClassName(PAXSTORE_PACKAGENAME, PAXSTORE_DOWNLOADLIST_PAGE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get PAXSTORE PUSH online status.
     * @param context
     * @return
     */
    public OnlineStatusInfo getOnlineStatusFromPAXSTORE(Context context) {
        OnlineStatusInfo onlineStatusInfo = new OnlineStatusInfo();
        //对location表进行操作
        // 和上述类似,只是URI需要更改,从而匹配不同的URI CODE,从而找到不同的数据资源
        Uri uri_location = Uri.parse("content://com.pax.market.android.app/online_status");
        // 获取ContentResolver
        ContentResolver resolver = context.getContentResolver();
        // 通过ContentResolver 根据URI 向ContentProvider中插入数据
        // 通过ContentResolver 向ContentProvider中查询数据
        Cursor cursor = resolver.query(uri_location, null, null, null, null);
        if (cursor == null) {
            onlineStatusInfo.setOnline(null);
            onlineStatusInfo.setBusinessCode(QueryResult.QUERY_FROM_CONTENT_PROVIDER_FAILED.getCode());
            onlineStatusInfo.setMessage(QueryResult.QUERY_FROM_CONTENT_PROVIDER_FAILED.getMsg());
            return onlineStatusInfo;
        }
        while (cursor.moveToNext()) {
            System.out.println("query job:" + cursor.getInt(0) + " " + cursor.getString(1)
                    + " " + cursor.getString(2));
            onlineStatusInfo.setBusinessCode(cursor.getInt(0));
            onlineStatusInfo.setMessage(cursor.getString(1));
            Boolean onlineStatus = (cursor.getString(2) != null ?
                    Boolean.valueOf(cursor.getString(2)) : null);
            onlineStatusInfo.setOnline(onlineStatus);
            // 将表中数据全部输出
        }
        // 关闭游标
        cursor.close();

        return onlineStatusInfo;
    }

    public interface LocationCallBack {
        void onLocationRetured(LocationInfo locationInfo);
    }

    /**
     * callback of update inquirer {@link #initInquirer}
     * this method will tell store app that whether your app can be updated.
     */
    public interface Inquirer {
        boolean isReadyUpdate();
    }

    /**
     * Get location from PAXSTORE.
     * @param context
     * @param locationCallback
     */
    public void startLocate(Context context, LocationService.LocationCallback locationCallback) {
        LocationService.setCallback(locationCallback);
        Intent intent = new Intent(context, LocationService.class);
        intent.setPackage(BuildConfig.APPLICATION_ID);
        context.startService(intent);
        //如果启动service失败，有可能没有结果返回，测试需要让它自动返回一个timeout的结果。
    }



}
