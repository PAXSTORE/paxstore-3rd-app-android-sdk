package com.pax.market.android.app.sdk;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.pax.market.android.app.sdk.dto.QueryResult;
import com.pax.market.android.app.sdk.dto.TerminalInfo;
import com.pax.market.android.app.sdk.util.PreferencesUtils;
import com.pax.market.api.sdk.java.api.sync.SyncApi;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.MerchantObject;
import com.pax.market.api.sdk.java.base.dto.SdkObject;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SyncApiStrategy extends SyncApi {
    private static final String TAG = SyncApiStrategy.class.getSimpleName();
    private Context context;
    private final String API_MERCHANT_INFO_PERMISSION = "com.market.android.app.API_MERCHANT_INFO";

    public SyncApiStrategy(Context context,String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
        this.context = context;
    }


    private SdkObject checkPermission(String neededPermission) {
        SdkObject checkResult = null;
        boolean permissionRequested = false;
        PackageManager packageManager = context.getPackageManager();
        try {
            String[] requestedPermissions = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;
            if (!(requestedPermissions == null || requestedPermissions.length == 0)) {
                for (String requestedPermission : requestedPermissions) {
                    if (neededPermission.equals(requestedPermission)) {
                        permissionRequested = true;
                    }
                }
            } // return fail  if no permission
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "e");
        }
        if (!permissionRequested) {
            checkResult = new SdkObject();
            checkResult.setBusinessCode(QueryResult.PERMISSION_NOT_REQUESTED.getCode());
            checkResult.setMessage(QueryResult.PERMISSION_NOT_REQUESTED.getMsg() + neededPermission);
            Log.w(TAG, QueryResult.PERMISSION_NOT_REQUESTED.getMsg() + neededPermission);
        }
        return checkResult;
    }

    @Override
    public MerchantObject getMerchantInfo() {
        MerchantObject merchantObject = new MerchantObject();

        SdkObject sdkObject = checkPermission(API_MERCHANT_INFO_PERMISSION);
        if (sdkObject != null) {
            merchantObject.setMessage(sdkObject.getMessage());
            merchantObject.setBusinessCode(sdkObject.getBusinessCode());
            return merchantObject;
        }

        long lastMerchantTime = PreferencesUtils.getLong(context, CommonConstants.SP_LAST_GET_MERCHANT_TIME, 0L);
        if (System.currentTimeMillis() - lastMerchantTime < 1000L) { //Ignore call within 1 second
            merchantObject.setBusinessCode(QueryResult.GET_MERCHANT_TOO_FAST.getCode());
            merchantObject.setMessage(QueryResult.GET_MERCHANT_TOO_FAST.getMsg());
            return merchantObject;
        }
        PreferencesUtils.putLong(context, CommonConstants.SP_LAST_GET_MERCHANT_TIME, System.currentTimeMillis());
        MerchantObject notAllowResult = checkAllowGetTerminalInfo(merchantObject, sdkObject);
        if (notAllowResult != null) return notAllowResult;

        merchantObject = super.getMerchantInfo();
        return merchantObject;
    }

    private MerchantObject checkAllowGetTerminalInfo(MerchantObject merchantObject, SdkObject sdkObject) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TerminalInfo terminalInfo = new TerminalInfo();
        BaseApiService.getInstance(context).getBaseTerminalInfo(new BaseApiService.ICallBack() {

            @Override
            public void onSuccess(Object obj) {
                TerminalInfo item = (TerminalInfo) obj;
                terminalInfo.setBussinessCode(item.getBussinessCode());
                terminalInfo.setMessage(item.getMessage());
                countDownLatch.countDown();
            }

            @Override
            public void onError(Exception e) {
                terminalInfo.setBussinessCode(-1);
                terminalInfo.setMessage("aidl error from STORE CLIENT: " + e);
                Log.e("getMerchantInfo", "aidl error from STORE CLIENT: " + e);
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e("getMerchantInfo", "countDownLatch InterruptedException e:" + e);
        }

        if (terminalInfo.getBussinessCode() != ResultCode.SUCCESS.getCode()) {
            merchantObject.setMessage(sdkObject.getMessage());
            merchantObject.setBusinessCode(sdkObject.getBusinessCode());
            return merchantObject;
        }
        return null;
    }
}
