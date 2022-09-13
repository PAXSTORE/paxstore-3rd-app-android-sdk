package com.pax.market.android.app.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.pax.market.android.app.aidl.IApiUrlService;
import com.pax.market.android.app.aidl.IRemoteSdkService;
import com.pax.market.android.app.sdk.dto.DcUrlInfo;
import com.pax.market.android.app.sdk.dto.QueryResult;
import com.pax.market.android.app.sdk.dto.StoreProxyInfo;
import com.pax.market.android.app.sdk.dto.TerminalInfo;
import com.pax.market.android.app.sdk.util.PreferencesUtils;
import com.pax.market.api.sdk.java.base.client.ProxyDelegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import static com.pax.market.android.app.sdk.CommonConstants.ERR_MSG_BIND_PAXSTORE_SERVICE_FAILED;
import static com.pax.market.android.app.sdk.CommonConstants.ERR_MSG_NULL_RETURNED;
import static com.pax.market.android.app.sdk.CommonConstants.ERR_MSG_PAXSTORE_MAY_NOT_INSTALLED;

/**
 * Created by fojut on 2017/11/30.
 */
public class BaseApiService implements ProxyDelegate {
    private static final Logger logger = LoggerFactory.getLogger(BaseApiService.class);

    private static final String SP_FILE_NAME = "store.sdk.cfg";
    private static final String SP_STORE_PROXY_TYPE = "proxyType";
    private static final String SP_STORE_PROXY_HOST = "proxyHost";
    private static final String SP_STORE_PROXY_PORT = "proxyPort";
    private static final String SP_STORE_PROXY_AUTH = "proxyAuthorization";
    private static final String SP_STORE_PROXY_USER = "proxyUsername";
    private static final String SP_STORE_PROXY_PASS = "proxyPassword";
    private static final String GET_TERMINAL_INFO_ACTION = "com.pax.market.android.app.aidl.REMOTE_SDK_SERVICE";
    public static final String INIT_ACTION = "com.pax.market.android.app.aidl.API_URL_SERVICE";
    public static final String PAXSTORE_PACKAGE_NAME = "com.pax.market.android.app";
    public final String ERR_GET_DC_URL_FAILED = "No baseUrl, please init first!";
    public final String ERR_GET_SN_FAILED = "Cannot get terminal serialNo, please update store client first";
    public final String VALUE_NULL = "NULL";
    private static volatile BaseApiService instance;
    private Context context;
    private SharedPreferences sp;
    private StoreProxyInfo storeProxy;

    private BaseApiService(Context context) {
        this.context = context;
        this.sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static BaseApiService getInstance(Context context) {
        if (instance == null) {
            synchronized (BaseApiService.class) {
                if (instance == null) {
                    instance = new BaseApiService(context);
                }
            }
        }
        return instance;
    }

    public void init(final String appKey, final String appSecret,
                     final Callback callback1, final ApiCallBack apiCallBack) {

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    StoreProxyInfo proxyInfo = IApiUrlService.Stub.asInterface(service).getStoreProxyInfo();
                    if (proxyInfo != null) {
                        logger.info(">>> Init proxy from STORE client : proxy[@{}/{}:{}], proxy authentication={}",
                                proxyInfo.getType() == 1 ? "HTTP" : proxyInfo.getType() == 2 ? "SOCKS" : "DIRECT",
                                proxyInfo.getHost(), proxyInfo.getPort(),
                                proxyInfo.getAuthorization() != null ? "Basic" : proxyInfo.getUsername() != null ? "Password" : "NULL");
                    } else {
                        logger.warn(">>> Init proxy from PASXTORE : [NULL]");
                    }
                    setStoreProxyInfo(proxyInfo);
                    String terminalSn = IApiUrlService.Stub.asInterface(service).getSn();
                    String apiUrl = null;
                    if (terminalSn == null) {
                        terminalSn = getSN();
                        apiUrl = IApiUrlService.Stub.asInterface(service).getApiUrl();
                    }
                    String model = IApiUrlService.Stub.asInterface(service).getModel();
                    new InitApiAsyncTask().execute(new InitApiParams(apiCallBack, callback1, apiUrl, terminalSn, model));
                } catch (RemoteException e) {
                    logger.error(">>> Get Api URL error", e);
                    callback1.initFailed(e);
                    apiCallBack.initFailed();
                }
                context.unbindService(this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                logger.error("onServiceDisconnected");
            }
        };

        Intent intent = new Intent(INIT_ACTION);
        intent.setPackage(PAXSTORE_PACKAGE_NAME);
        boolean bindResult = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!bindResult) {
            callback1.initFailed(new RemoteException(ERR_MSG_PAXSTORE_MAY_NOT_INSTALLED));
            apiCallBack.initFailed();
            context.unbindService(serviceConnection);
        }
    }

    private String getSN() {
        Log.w("BaseApiService", "Please update STORE client to latest version!!");
        // When it comes to Android 8+， you may not get serialNo by Build.SERIAL. Then you should update STORE client
        // client to the latest version to get SerialNo from STORE client.
        return Build.SERIAL;
    }


    public void getDcUrl(final DcCallBack callback1, final String oriBaseUrl) {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, final IBinder service) {
                try {
                    StoreProxyInfo proxyInfo = IApiUrlService.Stub.asInterface(service).getStoreProxyInfo();
                    if(proxyInfo != null) {
                        logger.info(">>> Get proxy from STORE client : proxy[@{}/{}:{}], proxy authentication={}",
                                proxyInfo.getType() == 1 ? "HTTP" : proxyInfo.getType() == 2 ? "SOCKS" : "DIRECT",
                                proxyInfo.getHost(), proxyInfo.getPort(),
                                proxyInfo.getAuthorization() != null ? "Basic" : proxyInfo.getUsername() != null ? "Password" : "NULL");
                    } else {
                        logger.warn(">>> Get proxy from PASXTORE : [NULL]");
                    }
                    setStoreProxyInfo(proxyInfo);

                    new InitDcUrlAsyncTask().execute(new DcApiParams(callback1, service, oriBaseUrl));
                } catch (RemoteException e) {
                    logger.error(">>> Get Api URL error", e);
                    callback1.initFailed(e);
                }
                context.unbindService(this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                logger.error("onServiceDisconnected");
            }
        };

        Intent intent = new Intent(INIT_ACTION);
        intent.setPackage(PAXSTORE_PACKAGE_NAME);
        boolean bindResult = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!bindResult) {
            callback1.initFailed(new RemoteException(ERR_MSG_PAXSTORE_MAY_NOT_INSTALLED));
            context.unbindService(serviceConnection);
        }
    }

    private class InitApiParams {
        Callback callback1;
        ApiCallBack apiCallBack;
        String terminalSn;
        String model;
        String apiUrl;

        InitApiParams(ApiCallBack apiCallBack, Callback callback1, String apiUrl, String terminalSn, String model) {
            this.callback1 = callback1;
            this.apiCallBack = apiCallBack;
            this.apiUrl = apiUrl;
            this.terminalSn = terminalSn;
            this.model = model;
        }
    }

    private class InitApiAsyncTask extends AsyncTask<InitApiParams, Void, Void> {

        @Override
        protected Void doInBackground(InitApiParams... initApiParams) {
            InitApiParams initApiParams1 = initApiParams[0];
            if (initApiParams1 == null) {
                return null;
            }
            if (initApiParams1.terminalSn == null || VALUE_NULL.equalsIgnoreCase(initApiParams1.terminalSn)) {
                Log.w("Init", "sn:" + initApiParams1.terminalSn);
                initApiParams1.apiCallBack.initFailed();
                initApiParams1.callback1.initFailed(new RemoteException(ERR_GET_SN_FAILED));
            } else {
                initApiParams1.apiCallBack.initSuccess(initApiParams1.apiUrl, initApiParams1.terminalSn, initApiParams1.model);
                initApiParams1.callback1.initSuccess();
            }
            return null;
        }
    }

    private class DcApiParams {
        DcCallBack dcCallBack;
        IBinder service;
        String oriBaseUrl;

        DcApiParams(DcCallBack callback1, IBinder service,  String oriBaseUrl) {
            this.dcCallBack = callback1;
            this.service = service;
            this.oriBaseUrl = oriBaseUrl;
        }
    }

    private class InitDcUrlAsyncTask extends AsyncTask<DcApiParams, Void, Void> {

        @Override
        protected Void doInBackground(DcApiParams... initApiParams) {
            DcApiParams dcCallBack = initApiParams[0];
            String oriBaseUrl = dcCallBack.oriBaseUrl;

            if (dcCallBack == null) {
                return null;
            }

            DcUrlInfo localDcUrlInfo = PreferencesUtils.getObject(context, CommonConstants.SP_LAST_GET_DCURL_TIME, DcUrlInfo.class);
            if (localDcUrlInfo != null && localDcUrlInfo.getDcUrl()!= null && !"null".equalsIgnoreCase(localDcUrlInfo.getDcUrl())
                    && System.currentTimeMillis() - localDcUrlInfo.getLastAccessTime() < CommonConstants.ONE_HOUR_INTERVAL) {
                dcCallBack.dcCallBack.initSuccess(localDcUrlInfo.getDcUrl());
                return null;
            }

            try {
                DcUrlInfo info = IApiUrlService.Stub.asInterface(dcCallBack.service).getDcUrlInfoByTid("");
                if (info == null) { // if info is null, it explains that the PAXSTORE client is lower than 8.0.0
                    info = IApiUrlService.Stub.asInterface(dcCallBack.service).getDcUrlInfo();
                }
                if (info == null) {
                    if (oriBaseUrl == null) { // when PAXSTORE client is lower than 8.0.0，cannot get dcurl, there supposed to have a default url
                        Log.e("InitDcUrlAsyncTask", ERR_GET_DC_URL_FAILED);
                        dcCallBack.dcCallBack.initFailed(new Exception(ERR_GET_DC_URL_FAILED));
                        return null;
                    }
                    info = new DcUrlInfo();
                    info.setDcUrl(oriBaseUrl);
                    info.setLastAccessTime(System.currentTimeMillis());
                }
                dcCallBack.dcCallBack.initSuccess(info.getDcUrl());
            } catch (RemoteException e) {
                Log.e("InitDcUrlAsyncTask", "e:" + e);
                dcCallBack.dcCallBack.initFailed(e);
            }
            return null;
        }
    }

    @Override
    public Proxy retrieveProxy() {
        Proxy proxy = null;
        StoreProxyInfo storeProxyInfo = getStoreProxyInfo();
        if(storeProxyInfo != null){
            switch (storeProxyInfo.getType()){
                case 1:     // HTTP
                    proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(storeProxyInfo.getHost(), storeProxyInfo.getPort()));
                    break;
                case 2:     // SOCKS
                    proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(storeProxyInfo.getHost(), storeProxyInfo.getPort()));
                    break;
                default:
                    proxy = Proxy.NO_PROXY;
                    break;
            }
        }
        return proxy;
    }

    @Override
    public String retrieveBasicAuthorization() {
        StoreProxyInfo storeProxyInfo = getStoreProxyInfo();
        return storeProxyInfo == null ? null : storeProxyInfo.getAuthorization();
    }

    @Override
    public PasswordAuthentication retrievePasswordAuthentication() {
        if (getStoreProxyInfo() == null || getStoreProxyInfo().getUsername() == null) {
            return null;
        }
        return new PasswordAuthentication(getStoreProxyInfo().getUsername(), getStoreProxyInfo().getPassword() == null ? "".toCharArray() : getStoreProxyInfo().getPassword());
    }

    public interface Callback {
        void initSuccess();

        void initFailed(RemoteException e);
    }


    public interface ApiCallBack {
        void initSuccess(String apiUrl, String terminalSn, String model);

        void initFailed();
    }

    public interface ICallBack {
        void onSuccess(Object obj);

        void onError(Exception e);
    }

    public interface DcCallBack {
        void initSuccess(String baseUrl);

        void initFailed(Exception e);
    }


    public void getBaseTerminalInfo(final ICallBack iCallBack) {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    TerminalInfo terminalInfo = IRemoteSdkService.Stub.asInterface(service).getBaseTerminalInfo();
                    if(terminalInfo == null || terminalInfo.getTid()==null || terminalInfo.getTid().isEmpty()){
                        if (terminalInfo.getBussinessCode() == QueryResult.GET_INFO_NOT_ALLOWED.getCode()) {
                            iCallBack.onError(new RemoteException(QueryResult.GET_INFO_NOT_ALLOWED.getMsg()));
                        } else {
                            iCallBack.onError(new RemoteException(ERR_MSG_NULL_RETURNED));
                        }
                    }else {
                        iCallBack.onSuccess(terminalInfo);
                    }
                } catch (RemoteException e) {
                    logger.error(">>> getBaseTerminalInfo error", e);
                    iCallBack.onError(new RemoteException(ERR_MSG_BIND_PAXSTORE_SERVICE_FAILED));
                }
                context.unbindService(this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                logger.error("onServiceDisconnected");
            }
        };

        Intent intent = new Intent(GET_TERMINAL_INFO_ACTION);
        intent.setPackage(PAXSTORE_PACKAGE_NAME);
        boolean bindResult = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!bindResult) {
            iCallBack.onError(new RemoteException(ERR_MSG_BIND_PAXSTORE_SERVICE_FAILED));
            context.unbindService(serviceConnection);
        }
    }


    public void setStoreProxyInfo(StoreProxyInfo storeProxyInfo){
        if(storeProxyInfo == null){
            if (this.storeProxy != null) {
                SharedPreferences.Editor editor = sp.edit();
                editor.remove(SP_STORE_PROXY_TYPE)
                        .remove(SP_STORE_PROXY_HOST)
                        .remove(SP_STORE_PROXY_PORT)
                        .remove(SP_STORE_PROXY_AUTH)
                        .remove(SP_STORE_PROXY_USER)
                        .remove(SP_STORE_PROXY_PASS)
                        .apply();
            }
        } else {
            if (!storeProxyInfo.equals(this.storeProxy)) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(SP_STORE_PROXY_TYPE, storeProxyInfo.getType())
                        .putString(SP_STORE_PROXY_HOST, storeProxyInfo.getHost())
                        .putInt(SP_STORE_PROXY_PORT, storeProxyInfo.getPort())
                        .putString(SP_STORE_PROXY_AUTH, storeProxyInfo.getAuthorization())
                        .putString(SP_STORE_PROXY_USER, storeProxyInfo.getUsername())
                        .putString(SP_STORE_PROXY_PASS, storeProxyInfo.getPassword() == null ? null : String.copyValueOf(storeProxyInfo.getPassword()))
                        .apply();
            }
        }
        this.storeProxy = storeProxyInfo;
    }

    public StoreProxyInfo getStoreProxyInfo(){
        if(this.storeProxy == null && sp.getInt(SP_STORE_PROXY_TYPE, -1) != -1){
            StoreProxyInfo storeProxyInfo = new StoreProxyInfo();
            storeProxyInfo.setType(sp.getInt(SP_STORE_PROXY_TYPE, -1));
            storeProxyInfo.setHost(sp.getString(SP_STORE_PROXY_HOST, null));
            storeProxyInfo.setPort(sp.getInt(SP_STORE_PROXY_PORT, 0));
            storeProxyInfo.setAuthorization(sp.getString(SP_STORE_PROXY_AUTH, null));
            storeProxyInfo.setUsername(sp.getString(SP_STORE_PROXY_USER, null));
            String password = sp.getString(SP_STORE_PROXY_PASS, null);
            storeProxyInfo.setPassword(password != null ? password.toCharArray() : null);
            this.storeProxy = storeProxyInfo;
        }
        return this.storeProxy;
    }
}
