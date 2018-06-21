package com.pax.market.android.app.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.pax.market.android.app.aidl.IApiUrlService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fojut on 2017/11/30.
 */
public class BaseApiService {
    private static final Logger logger = LoggerFactory.getLogger(BaseApiService.class);


    private static volatile BaseApiService instance;
    private Context context;
    private IApiUrlService apiUrlService;

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

        if (apiUrlService == null) {
            ServiceConnection serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    apiUrlService = IApiUrlService.Stub.asInterface(service);
                    try {
                        String apiUrl = apiUrlService.getApiUrl();
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
                    apiUrlService = null;
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
        } else {
            try {
                String apiUrl = apiUrlService.getApiUrl();
                callback1.initSuccess();
                apiCallBack.initSuccess(apiUrl);
            } catch (RemoteException e) {
                logger.error(">>> Get Api URL error", e);
                callback1.initFailed(e);
                apiCallBack.initFailed();
            }
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

}
