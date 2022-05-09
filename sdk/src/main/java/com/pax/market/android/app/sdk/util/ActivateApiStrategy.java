package com.pax.market.android.app.sdk.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.pax.market.android.app.aidl.IApiUrlService;
import com.pax.market.android.app.sdk.dto.DcUrlInfo;
import com.pax.market.api.sdk.java.api.activate.ActivateApi;
import com.pax.market.api.sdk.java.base.dto.SdkObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static com.pax.market.android.app.sdk.BaseApiService.INIT_ACTION;
import static com.pax.market.android.app.sdk.BaseApiService.PAXSTORE_PACKAGE_NAME;
import static com.pax.market.android.app.sdk.CommonConstants.ERR_MSG_PAXSTORE_MAY_NOT_INSTALLED;

public class ActivateApiStrategy extends ActivateApi {
    private static final String LAST_DOWNLOAD = "lastDownload";
    private Context context;

    public ActivateApiStrategy(Context context, String baseUrl, String appKey, String appSecret, String terminalSN, String model) {
        super(baseUrl, appKey, appSecret, terminalSN, model);
        this.context = context;
    }

    public SdkObject initByTID(String tid) {
        //先根据tid去找一下dcurl.
        if (tid == null || tid.isEmpty()) {
            SdkObject sdkObject = new SdkObject();
            sdkObject.setBusinessCode(-1);
            sdkObject.setMessage("Tid should not be empty");
            return sdkObject;
        }
        // 把tid给PAXSTORE client， 让client去根据tid搜索dcurl
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final DcUrlInfo dcUrlInfo = new DcUrlInfo();
        getDcUrlByTid(new DcCallBack() {
            @Override
            public void initResult(DcUrlInfo result) {
                dcUrlInfo.setMessage(result.getMessage());
                dcUrlInfo.setBusinessCode(result.getBusinessCode());
                dcUrlInfo.setDcUrl(result.getDcUrl());
                dcUrlInfo.setLastAccessTime(result.getLastAccessTime());
                dcUrlInfo.setStaticUrl(result.getStaticUrl());
                countDownLatch.countDown();
            }
        }, tid);

        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, " e:" + e);
        }
        if (dcUrlInfo.getDcUrl() == null || "null".equalsIgnoreCase(dcUrlInfo.getDcUrl())) {
            SdkObject sdkObject = new SdkObject();
            sdkObject.setMessage(dcUrlInfo.getMessage());
            sdkObject.setBusinessCode(dcUrlInfo.getBusinessCode());
            return sdkObject;
        } else {
            setBaseUrl(dcUrlInfo.getDcUrl());
        }

        return super.initByTID(tid, dcUrlInfo.getDcUrl(), dcUrlInfo.getStaticUrl());
    }

    public interface DcCallBack {
        void initResult(DcUrlInfo dcUrlInfo);
    }

    public void getDcUrlByTid(final DcCallBack callback, final String tid) {
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, final IBinder service) {

                new InitDcUrlAsyncTask().execute(new DcApiParams(callback, service, tid));

                context.unbindService(this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected");
            }
        };

        Intent intent = new Intent(INIT_ACTION);
        intent.setPackage(PAXSTORE_PACKAGE_NAME);
        boolean bindResult = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!bindResult) {
            aidlFailed(callback);
            context.unbindService(serviceConnection);
        }
    }

    private class DcApiParams {
        DcCallBack dcCallBack;
        IBinder service;
        String tid;

        DcApiParams(DcCallBack callback1, IBinder service, String tid) {
            this.dcCallBack = callback1;
            this.service = service;
            this.tid = tid;
        }
    }


    private class InitDcUrlAsyncTask extends AsyncTask<DcApiParams, Void, Void> {

        @Override
        protected Void doInBackground(DcApiParams... dcApiParams) {
            DcApiParams dcCallBack = dcApiParams[0];
            String tid = dcCallBack.tid;

            if (dcCallBack == null) {
                return null;
            }

            try {
                DcUrlInfo info = IApiUrlService.Stub.asInterface(dcCallBack.service).getDcUrlInfoByTid(tid);
                if (info == null) {

                    info = new DcUrlInfo();
                    info.setDcUrl(null);
                    info.setLastAccessTime(System.currentTimeMillis());
                    info.setBusinessCode(-1);
                    info.setMessage("Get null from PAXSTORE client");
                }

                dcCallBack.dcCallBack.initResult(info);
            } catch (RemoteException e) {

                Log.e("InitDcUrlAsyncTask", "e:" + e);
                DcUrlInfo info = new DcUrlInfo();
                info.setBusinessCode(-1);
                info.setMessage(e.getMessage());
                dcCallBack.dcCallBack.initResult(info);
            }
            return null;
        }
    }

    /**
     * 与PAXSTORE 通信失败
     * @param callback
     */
    private void aidlFailed(DcCallBack callback) {
        DcUrlInfo dcUrlInfo = new DcUrlInfo();
        dcUrlInfo.setBusinessCode(-1);
        dcUrlInfo.setMessage(ERR_MSG_PAXSTORE_MAY_NOT_INSTALLED);
        callback.initResult(dcUrlInfo);
    }
}
