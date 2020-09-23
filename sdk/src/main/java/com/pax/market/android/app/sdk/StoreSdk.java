package com.pax.market.android.app.sdk;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import com.pax.market.android.app.sdk.dto.DcUrlInfo;
import com.pax.market.android.app.sdk.dto.LocationInfo;
import com.pax.market.android.app.sdk.dto.MediaMesageInfo;
import com.pax.market.android.app.sdk.dto.OnlineStatusInfo;
import com.pax.market.android.app.sdk.dto.QueryResult;
import com.pax.market.android.app.sdk.dto.StoreProxyInfo;
import com.pax.market.android.app.sdk.util.PreferencesUtils;
import com.pax.market.api.sdk.java.api.activate.ActivateApi;
import com.pax.market.api.sdk.java.api.sync.GoInsightApi;
import com.pax.market.api.sdk.java.api.sync.SyncApi;
import com.pax.market.api.sdk.java.api.update.UpdateApi;
import com.pax.market.api.sdk.java.base.client.ProxyDelegate;
import com.pax.market.api.sdk.java.base.exception.NotInitException;
import com.pax.market.api.sdk.java.base.util.CryptoUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static com.pax.market.android.app.sdk.CommonConstants.ERR_MSG_BIND_PAXSTORE_SERVICE_TOO_FAST;

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
    private ParamApiStrategy paramApi;
    private SyncApi syncApi;
    private GoInsightApi goInsightApi;
    private UpdateApi updateApi;
    private ActivateApi activateApi;
    private Context context;

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
     * @param callback
     */
    public void init(final Context context, final String appKey, final String appSecret,
                     final BaseApiService.Callback callback) throws NullPointerException {
        if (paramApi == null && syncApi == null && updateApi == null
                && activateApi == null && semaphore.availablePermits() != 1) {
            validParams(context, appKey, appSecret);
            this.context = context;
            this.appKey = appKey;
            this.appSecret = appSecret;
            try {
                logger.debug("init acquire 1");
                semaphore.acquire(1);
            } catch (InterruptedException e) {
                logger.error("e:" + e);
            }
            BaseApiService.getInstance(context).init(appKey, appSecret, callback,
                    new BaseApiService.ApiCallBack() {

                        @Override
                        public void initSuccess(String terminalSerialNo) {
                            Log.e("StoreSdk ttt", terminalSerialNo);
                            initApi(context, null, appKey, appSecret, terminalSerialNo, BaseApiService.getInstance(context));
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
    public ParamApiStrategy paramApi() throws NotInitException {
        if (paramApi == null) {
            acquireSemaphore();
            if (paramApi == null) {
                throw new NotInitException("Not initialized");
            }
        }
        paramApi.setBaseUrl(getDcUrl(context));
        paramApi.setProxyDelegate(BaseApiService.getInstance(context));
        return paramApi;
    }

    /**
     * Get activateApi instance
     *
     * @return
     * @throws NotInitException
     */
    public ActivateApi activateApi() throws NotInitException {
        if (activateApi == null) {
            acquireSemaphore();
            if (activateApi == null) {
                throw new NotInitException("Not initialized");
            }
        }
        activateApi.setBaseUrl(getDcUrl(context));
        activateApi.setProxyDelegate(BaseApiService.getInstance(context));
        return activateApi;
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
        syncApi.setBaseUrl(getDcUrl(context));
        syncApi.setProxyDelegate(BaseApiService.getInstance(context));
        return syncApi;
    }

    public GoInsightApi goInsightApi() throws NotInitException {
        if (goInsightApi == null) {
            acquireSemaphore();
            if (goInsightApi == null) {
                throw new NotInitException("Not initialized");
            }
        }
        goInsightApi.setBaseUrl(getDcUrl(context));
        goInsightApi.setProxyDelegate(BaseApiService.getInstance(context));
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
        updateApi.setBaseUrl(getDcUrl(context));
        updateApi.setProxyDelegate(BaseApiService.getInstance(context));
        return updateApi;
    }

    /**
     * Check if initialized
     * true: initialized
     *
     * @return
     */
    public boolean checkInitialization() {
        if (paramApi != null && syncApi != null && updateApi != null && activateApi != null) {
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
     * @throws NullPointerException
     */
    private void validParams(Context context, String appKey, String appSecret) throws NullPointerException {
        if (context == null) {
            throw new NullPointerException("Context needed");
        }
        if (appKey == null || appKey.isEmpty()) {
            throw new NullPointerException("AppKey needed");
        }
        if (appSecret == null || appSecret.isEmpty()) {
            throw new NullPointerException("AppSecret needed");
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
    public void initApi(Context context, String apiUrl, String appKey, String appSecret, String terminalSerialNo, ProxyDelegate proxyDelegate) {
        paramApi = new ParamApiStrategy(context, apiUrl, appKey, appSecret, terminalSerialNo).setProxyDelegate(proxyDelegate);
        syncApi = new SyncApi(apiUrl, appKey, appSecret, terminalSerialNo).setProxyDelegate(proxyDelegate);
        updateApi = new UpdateApi(apiUrl, appKey, appSecret, terminalSerialNo).setProxyDelegate(proxyDelegate);
        goInsightApi = new GoInsightApi(apiUrl, appKey, appSecret, terminalSerialNo, TimeZone.getDefault()).setProxyDelegate(proxyDelegate);
        activateApi = new ActivateApi(apiUrl, appKey, appSecret, terminalSerialNo).setProxyDelegate(proxyDelegate);
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
        long lastGetBaseInfo = PreferencesUtils.getLong(context, CommonConstants.SP_LAST_GET_TERMINAL_INFO_TIME, 0L);
        if (System.currentTimeMillis() - lastGetBaseInfo < 1000L) { //Ignore call within 1 second
            callback.onError(new RemoteException(ERR_MSG_BIND_PAXSTORE_SERVICE_TOO_FAST));
            return;
        }
        PreferencesUtils.putLong(context, CommonConstants.SP_LAST_GET_TERMINAL_INFO_TIME, System.currentTimeMillis());

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
        if (activateApi != null) {
            activateApi.setProxyDelegate(BaseApiService.getInstance(context));
        } else {
            logger.warn("activateApi is not initialized, please init StoreSdk first...");
        }
    }

    public String aesDecrypt(String encryptedData) {
        if (appSecret == null) {
            logger.error("Store sdk not initialized");
        }
        return CryptoUtils.aesDecrypt(encryptedData, appSecret);
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
     *
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
     *
     * @param context
     * @return
     */
    public OnlineStatusInfo getOnlineStatusFromPAXSTORE(Context context) {
        OnlineStatusInfo onlineStatusInfo = new OnlineStatusInfo();
        long lastSdkOnlineStatusTime = PreferencesUtils.getLong(context, CommonConstants.SP_LAST_GET_ONLINE_STATUS_TIME, 0L);
        if (System.currentTimeMillis() - lastSdkOnlineStatusTime < 1000L) { //Ignore call within 1 second
            onlineStatusInfo.setBusinessCode(QueryResult.GET_ONLINE_STATUS_TOO_FAST.getCode());
            onlineStatusInfo.setMessage(QueryResult.GET_ONLINE_STATUS_TOO_FAST.getMsg());
            Log.w("StoreSdk", QueryResult.GET_ONLINE_STATUS_TOO_FAST.getMsg());
            return onlineStatusInfo;
        }
        PreferencesUtils.putLong(context, CommonConstants.SP_LAST_GET_ONLINE_STATUS_TIME, System.currentTimeMillis());

        //对location表进行操作
        // 和上述类似,只是URI需要更改,从而匹配不同的URI CODE,从而找到不同的数据资源
        Uri uri_online_status = Uri.parse("content://com.pax.market.android.app/online_status");
        // 获取ContentResolver
        ContentResolver resolver = context.getContentResolver();
        // 通过ContentResolver 根据URI 向ContentProvider中插入数据
        // 通过ContentResolver 向ContentProvider中查询数据
        Cursor cursor = resolver.query(uri_online_status, null, null, null, null);
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
        }
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
     * Get location from PAXSTORE. （from provider)
     * if can not get loation from provider, get location from old service.
     *
     * @param context
     * @param locationCallback
     */
    public void startLocate(Context context, LocationService.LocationCallback locationCallback) {
        LocationInfo locationInfo = new LocationInfo();
        long lastSdkLocateTime = PreferencesUtils.getLong(context, CommonConstants.SP_LAST_GET_LOCATION_TIME, 0L);
        if (System.currentTimeMillis() - lastSdkLocateTime < 1000L) { //Ignore call within 1 second
            locationInfo.setBusinessCode(QueryResult.GET_LOCATION_TOO_FAST.getCode());
            locationInfo.setMessage(QueryResult.GET_LOCATION_TOO_FAST.getMsg());
            locationCallback.locationResponse(locationInfo);
            Log.w("StoreSdk", QueryResult.GET_LOCATION_TOO_FAST.getMsg());
            return;
        }

        PreferencesUtils.putLong(context, CommonConstants.SP_LAST_GET_LOCATION_TIME, System.currentTimeMillis());

        Uri uri_location = Uri.parse("content://com.pax.market.android.app/location");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri_location, null, null, null, null);
        if (cursor == null) {
            LocationService.setCallback(locationCallback);
            Intent intent = new Intent(context, LocationService.class);
            intent.setPackage(BuildConfig.APPLICATION_ID);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
            return;
        } else {
            while (cursor.moveToNext()) {
                System.out.println("query job:" + cursor.getInt(0) + " " + cursor.getString(1)
                        + " " + cursor.getString(2) + " " + cursor.getString(3) + " " + cursor.getString(4)
                        + " " + cursor.getString(5));
                locationInfo.setBusinessCode(cursor.getInt(0));
                locationInfo.setMessage(cursor.getString(1));
                locationInfo.setLongitude(cursor.getString(2));
                locationInfo.setLatitude(cursor.getString(3));
                locationInfo.setAccuracy(cursor.getString(4));
                Long lastLocateTime = (cursor.getString(5) != null ?
                        Long.valueOf(cursor.getString(5)) : null);
                locationInfo.setLastLocateTime(lastLocateTime);
            }
            cursor.close();

            locationCallback.locationResponse(locationInfo);
        }
    }

    public MediaMesageInfo getMediaMessage(Context context) {
        return PreferencesUtils.getObject(context, PushConstants.MEDIA_MESSAGE, MediaMesageInfo.class);
    }

    public String getDcUrl(final Context context) throws NotInitException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new NotInitException("Can not do this on MainThread!!");
        }

        final StringBuilder dcUrl = new StringBuilder();
        Log.e("StoreSdk", "ttt 0");


        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BaseApiService.getInstance(context).getDcUrl(new BaseApiService.DcCallBack() {

            @Override
            public void initSuccess(String baseUrl) {
                Log.e("StoreSdk", "ttt 1");
                DcUrlInfo dcUrlInfo1 = new DcUrlInfo();
                dcUrlInfo1.setDcUrl(baseUrl);
                dcUrlInfo1.setLastAccessTime(System.currentTimeMillis());
                PreferencesUtils.putObject(context, CommonConstants.SP_LAST_GET_DCURL_TIME, dcUrlInfo1);
                dcUrl.append(baseUrl);
                countDownLatch.countDown();
            }

            @Override
            public void initFailed(Exception e) {
                Log.e("StoreSdk", "ttt 2");
                Log.e("StoreSdk", "e:" + e);
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "e:" + e);
        }
        Log.e("StoreSdk", "ttt 3");
        if (dcUrl.toString().isEmpty() || dcUrl.toString().equalsIgnoreCase("null")) {
            throw new NotInitException("Get baseUrl failed");
        }
        Log.e("StoreSdk ttt ", dcUrl.toString());
        return dcUrl.toString();
    }

}
