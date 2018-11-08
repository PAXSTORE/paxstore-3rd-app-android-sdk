package com.pax.market.android.app.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.pax.market.android.app.aidl.IApiUrlService;
import com.pax.market.android.app.aidl.IRemoteSdkService;
import com.pax.market.android.app.sdk.dto.TerminalInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fojut on 2017/11/30.
 */
public class BaseApiService {
    private static final Logger logger = LoggerFactory.getLogger(BaseApiService.class);


    private static volatile BaseApiService instance;
    private Context context;

    private BaseApiService(Context context) {
        this.context = context;
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

        Intent intent = new Intent("com.pax.market.android.app.aidl.API_URL_SERVICE");
        intent.setPackage("com.pax.market.android.app");
        boolean bindResult = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!bindResult) {
            callback1.initFailed(new RemoteException("Bind service failed, PAXSTORE may not installed"));
            apiCallBack.initFailed();
            context.unbindService(serviceConnection);
        }
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
                        iCallBack.onError(new RemoteException("Null value returned, PAXSTORE may not activated or not login. Please check"));
                    }else {
                        iCallBack.onSuccess(terminalInfo);
                    }
                } catch (RemoteException e) {
                    logger.error(">>> getBaseTerminalInfo error", e);
                    iCallBack.onError(new RemoteException("Bind service failed, PAXSTORE may not installed or started. Please check"));
                }
                context.unbindService(this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                logger.error("onServiceDisconnected");
            }
        };

        Intent intent = new Intent("com.pax.market.android.app.aidl.REMOTE_SDK_SERVICE");
        intent.setPackage("com.pax.market.android.app");
        boolean bindResult = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!bindResult) {
            iCallBack.onError(new RemoteException("Bind service failed, PAXSTORE may not installed or started. Please check"));
            context.unbindService(serviceConnection);
        }
    }

}
