package com.pax.market.android.app.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;

import com.pax.market.android.app.aidl.IApiUrlService;
import com.pax.market.android.app.aidl.IRemoteSdkService;
import com.pax.market.android.app.sdk.dto.QueryResult;
import com.pax.market.android.app.sdk.dto.StoreProxyInfo;
import com.pax.market.android.app.sdk.dto.TerminalInfo;
import com.pax.market.api.sdk.java.base.client.ProxyDelegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
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
    private static final String GET_TERMINAL_INFO_ACTION = "com.pax.market.android.app.aidl.REMOTE_SDK_SERVICE";
    private static final String INIT_ACTION = "com.pax.market.android.app.aidl.API_URL_SERVICE";
    private static final String PAXSTORE_PACKAGE_NAME = "com.pax.market.android.app";


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

    public void init(final String appKey, final String appSecret, final String terminalSerialNo,
                     final Callback callback1, final ApiCallBack apiCallBack) {

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    StoreProxyInfo proxyInfo = IApiUrlService.Stub.asInterface(service).getStoreProxyInfo();
                    if(proxyInfo != null) {
                        logger.info(">>> Init proxy from PAXSTORE : proxy[@{}/{}:{}], has proxy authenticator={}",
                                proxyInfo.getType() == 1 ? "HTTP" : proxyInfo.getType() == 2 ? "SOCKS" : "DIRECT",
                                proxyInfo.getHost(), proxyInfo.getPort(), proxyInfo.getAuthorization() != null);
                    } else {
                        logger.warn(">>> Init proxy from PASXTORE : [NULL]");
                    }
                    setStoreProxyInfo(proxyInfo);
                    String apiUrl = IApiUrlService.Stub.asInterface(service).getApiUrl();
                    apiCallBack.initSuccess(apiUrl);
                    callback1.initSuccess();
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
    public String retrieveProxyAuthorization() {
        StoreProxyInfo storeProxyInfo = getStoreProxyInfo();
        return storeProxyInfo == null ? null : storeProxyInfo.getAuthorization();
    }


    public interface Callback {
        void initSuccess();

        void initFailed(RemoteException e);
    }


    public interface ApiCallBack {
        void initSuccess(String baseUrl);

        void initFailed();
    }

    public interface ICallBack {
        void onSuccess(Object obj);

        void onError(Exception e);
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
        SharedPreferences.Editor editor = sp.edit();
        if(storeProxyInfo == null){
            editor.remove(SP_STORE_PROXY_TYPE)
                    .remove(SP_STORE_PROXY_HOST)
                    .remove(SP_STORE_PROXY_PORT)
                    .remove(SP_STORE_PROXY_AUTH);
        } else {
            editor.putInt(SP_STORE_PROXY_TYPE, storeProxyInfo.getType())
                    .putString(SP_STORE_PROXY_HOST, storeProxyInfo.getHost())
                    .putInt(SP_STORE_PROXY_PORT, storeProxyInfo.getPort())
                    .putString(SP_STORE_PROXY_AUTH, storeProxyInfo.getAuthorization());
        }
        editor.apply();
        this.storeProxy = storeProxyInfo;
    }

    public StoreProxyInfo getStoreProxyInfo(){
        if(this.storeProxy == null && sp.getInt(SP_STORE_PROXY_TYPE, -1) != -1){
            StoreProxyInfo storeProxyInfo = new StoreProxyInfo();
            storeProxyInfo.setType(sp.getInt(SP_STORE_PROXY_TYPE, -1));
            storeProxyInfo.setHost(sp.getString(SP_STORE_PROXY_HOST, null));
            storeProxyInfo.setPort(sp.getInt(SP_STORE_PROXY_PORT, 0));
            storeProxyInfo.setAuthorization(sp.getString(SP_STORE_PROXY_AUTH, null));
            this.storeProxy = storeProxyInfo;
        }
        return this.storeProxy;
    }
}
