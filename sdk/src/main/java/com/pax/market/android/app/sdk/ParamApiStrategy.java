package com.pax.market.android.app.sdk;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pax.market.android.app.sdk.util.NetWorkUtils;
import com.pax.market.android.app.sdk.util.PreferencesUtils;
import com.pax.market.api.sdk.java.api.param.ParamApi;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.DownloadResultObject;
import com.pax.market.api.sdk.java.base.dto.InnerDownloadResultObject;
import com.pax.market.api.sdk.java.base.dto.LastFailObject;
import com.pax.market.api.sdk.java.base.dto.SdkObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ParamApiStrategy extends ParamApi {

    private static final String TAG = ParamApiStrategy.class.getSimpleName();
    private static final String LAST_DOWNLOAD = "lastDownload";
    private static final String KEY_DOWNLOADED_LIST = "downloadedIdList";
    private Context context;


    public ParamApiStrategy(Context context, String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
        this.context = context;
    }

    public DownloadResultObject downloadParamToPathWithSHA256Check(String packageName, int versionCode, String saveFilePath) {
        return downloadParams(packageName, versionCode, saveFilePath, true, false);
    }

    public DownloadResultObject downloadParamToPath(String packageName, int versionCode, String saveFilePath) {
        return downloadParams(packageName, versionCode, saveFilePath, false, false);
    }

    public DownloadResultObject downloadParamsSHA256(String packageName, int versionCode, String saveFilePath) {
        return downloadParams(packageName, versionCode, saveFilePath, true, true);
    }

    public DownloadResultObject downloadParam(String packageName, int versionCode, String saveFilePath) {
        return downloadParams(packageName, versionCode, saveFilePath, false, true);
    }

    public DownloadResultObject downloadParams(String packageName, int versionCode, String saveFilePath, 
                                                boolean verifySHA, boolean needApplyResult) {

        Log.w("StoreSdk", "StoreSdk start");
        boolean mobileNetAvailable = NetWorkUtils.isMobileNetAvailable(context);
        LastFailObject failTask = PreferencesUtils.getObject(context, LAST_DOWNLOAD, LastFailObject.class);
        InnerDownloadResultObject downloadResultObject = null;

        List<Long> downloadedList = needApplyResult ? getIdListFromPrefs() : null;
        if (verifySHA) {
            downloadResultObject = super.downloadParamsWithShaCheck(packageName,
                    versionCode, saveFilePath, failTask, mobileNetAvailable, needApplyResult, downloadedList);
        } else {
            downloadResultObject = super.downloadParamToPath(packageName,
                    versionCode, saveFilePath, failTask, mobileNetAvailable, needApplyResult, downloadedList);
        }


        if (downloadResultObject.getBusinessCode() == ResultCode.SDK_DOWNLOAD_IOEXCEPTION.getCode()) {
            // save one download IOException record
            PreferencesUtils.putObject(context, LAST_DOWNLOAD, downloadResultObject.getLastFailObject());
        } else {
            PreferencesUtils.remove(context, LAST_DOWNLOAD);
        }

        if (needApplyResult) {
            Log.i(TAG, "need apply result");
            saveIdListToPrefs(downloadResultObject.getActionList());
        }
        return mapToDownloadResult(saveFilePath, downloadResultObject);
    }

    private ArrayList<Long> getIdListFromPrefs() {
        String json = PreferencesUtils.getString(context, KEY_DOWNLOADED_LIST, null);
        if (json != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Long>>(){}.getType();
            return gson.fromJson(json, listType);
        }
        return null;
    }

    private void saveIdListToPrefs(List<Long> downloadedList) {
        Gson gson = new Gson();
        String json = gson.toJson(downloadedList);
        PreferencesUtils.putString(context, KEY_DOWNLOADED_LIST, json);
    }


    public DownloadResultObject downloadLastSuccessParamToPath(String saveFilePath, String paramTemplateName) {

        InnerDownloadResultObject downloadResultObject = super.downloadLastSuccessParmToPath(saveFilePath, paramTemplateName);

        return mapToDownloadResult(saveFilePath, downloadResultObject);
    }

    public DownloadResultObject downloadLastSuccessParamToPath(String saveFilePath) {
        return downloadLastSuccessParamToPath(saveFilePath, null);
    }

    public SdkObject syncApplySuccessResult(List<Long> actionIdList) {
        removeIdList(actionIdList); // 无Regardless of whether the update result is successful or not, the local record will be deleted. If the update fails, the process will be restarted from start to finish. The document tells the customer that if the update result fails, they need to ensure that the update result is successful, otherwise they will repeatedly download the parameters
        return updateApplyResult(actionIdList, ACT_STATUS_SUCCESS, CODE_NONE_ERROR, REMARKS_CODE_PARAM_APPLIED);
    }

    public SdkObject syncApplyFailureResult(List<Long> actionIdList, String remarks) {
        removeIdList(actionIdList);// Regardless of whether the update result is successful or not, the local record will be deleted. If the update fails, the process will be restarted from start to finish.
        return updateApplyResult(actionIdList, ACT_STATUS_FAILED, ERROR_CODE_PARAM_APPLY_FAILED, remarks);
    }

    private void removeIdList(List<Long> actionIdList) {
        ArrayList<Long> idListFromPrefs = getIdListFromPrefs();
        if (idListFromPrefs!= null) {
            idListFromPrefs.removeAll(actionIdList);
        }
        if (idListFromPrefs != null && !idListFromPrefs.isEmpty()) {
            saveIdListToPrefs(idListFromPrefs);
        } else {
            PreferencesUtils.remove(context, KEY_DOWNLOADED_LIST);
        }
    }

}
