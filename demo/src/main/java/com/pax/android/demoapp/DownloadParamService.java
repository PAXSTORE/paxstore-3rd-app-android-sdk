package com.pax.android.demoapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.pax.market.android.app.sdk.StoreSdk;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.DownloadResultObject;
import com.pax.market.api.sdk.java.base.exception.NotInitException;

import org.dom4j.DocumentException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zcy on 2016/12/2 0002.
 */
public class DownloadParamService extends Service {

    private static final String TAG = DownloadParamService.class.getSimpleName();
    public static String saveFilePath;
    private static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private SPUtil spUtil;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        spUtil=new SPUtil();
        //Specifies the download path for the parameter file, you can replace the path to your app's internal storage for security.
        saveFilePath = getFilesDir() + "/YourPath/";

        //show downloading info in main page
        updateUI(DemoConstants.DOWNLOAD_STATUS_START);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Call SDK API to download parameter files into your specific directory,
                DownloadResultObject downloadResult = null;
                try {
                    downloadResult = StoreSdk.getInstance().paramApi().downloadParamToPath(getApplication().getPackageName(), BuildConfig.VERSION_CODE, saveFilePath);
                    Log.d(TAG, downloadResult.toString());
                } catch (NotInitException e) {
                    Log.e(TAG, "e:" + e);
                }

//                businesscode==0, means download successful, if not equal to 0, please check the return message when need.
                if (downloadResult != null && downloadResult.getBusinessCode() == ResultCode.SUCCESS) {
                    //todo can start to add your logic.

                    //below is only for demo
                    handleSuccess();
                } else {
                    //update download fail info in main page for Demo
                    Log.e(TAG, "ErrorCode: "+downloadResult.getBusinessCode()+"ErrorMessage: "+downloadResult.getMessage());
                    spUtil.setString(DemoConstants.PUSH_RESULT_BANNER_TITLE, DemoConstants.DOWNLOAD_FAILED);
                    spUtil.setString(DemoConstants.PUSH_RESULT_BANNER_TEXT,"Your push parameters file task failed at "+ sdf.format(new Date())+", please check error log.");
                    updateUI(DemoConstants.DOWNLOAD_STATUS_FAILED);
                }

            }
        });
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleSuccess() {
        spUtil.setString(DemoConstants.PUSH_RESULT_BANNER_TITLE, DemoConstants.DOWNLOAD_SUCCESS);
        //file download to saveFilePath above.
        File parameterFile=null;
        File[] filelist = new File(saveFilePath).listFiles();
        if (filelist != null && filelist.length>0) {
            for(File f :filelist){
                //for demo only, here hard code the xml name to "sys_cap.p". this demo will only parse with the specified file name
                if(DemoConstants.DOWNLOAD_PARAM_FILE_NAME.equals(f.getName())){
                    parameterFile=f;
                }
            }
            if(parameterFile!=null){

                String bannerTextValue = "Your push parameters  - "+parameterFile.getName()
                        +" have been successfully pushed at "+ sdf.format(new Date())+".";
                String bannerSubTextValue = "Files are stored in "+parameterFile.getPath();
                Log.i(TAG, "run=====: "+bannerTextValue);
                //save result for demo display
                spUtil.setString(DemoConstants.PUSH_RESULT_BANNER_TEXT,bannerTextValue);
                spUtil.setString(DemoConstants.PUSH_RESULT_BANNER_SUBTEXT,bannerSubTextValue);
                try {
                    //parse the download parameter xml file for display.
                    List<Map<String, Object>> datalist = new ArrayList<>();

                    HashMap<String,String> resultMap = StoreSdk.getInstance().paramApi().parseDownloadParamXml(parameterFile);

                    if(resultMap!=null && resultMap.size()>0) {
                        //convert result map to list for ListView display.
                        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("label", entry.getKey());
                            map.put("value",entry.getValue());
                            datalist.add(map);
                        }
                    }

                    //save result for demo display
                    spUtil.setDataList(DemoConstants.PUSH_RESULT_DETAIL,datalist);
                }catch (NotInitException e) {
                    Log.e(TAG, "e:" + e);
                }catch (DocumentException e){
                    Log.e("MainActivity:", "parse xml failed: "+e.getMessage());
                }
            }else{
                spUtil.setString(DemoConstants.PUSH_RESULT_BANNER_TEXT,"Download file not found. This demo only accept parameter file with name 'sys_cap.p'");
            }
        }
        //update successful info in main page for Demo
        updateUI(DemoConstants.DOWNLOAD_STATUS_SUCCESS);
    }


    /**
     * notify MainActivity to display downloaded files, just for demo display
     */
    private void updateUI(int stateCode) {
        Intent intent = new Intent(DemoConstants.UPDATE_VIEW_ACTION);
        intent.putExtra(DemoConstants.DOWNLOAD_RESULT_CODE, stateCode);
        sendBroadcast(intent);
    }

}

