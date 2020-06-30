# PAXSTORE Downloading Parameter Integration

By integrating with this function, admin can dilivery parameters to the application.
### 1：Initialization of Sdk
Refer to the [SetUp](../README.md)

### 2：Download Parameters API

Register your IntentService

    <service android:name=".DownloadParamService">
        <intent-filter>
            <action android:name="com.sdk.service.ACTION_TO_DOWNLOAD_PARAMS"/>
            <category android:name="${applicationId}"/>
        </intent-filter>
    </service>

Download params in your IntentService. see [DownloadParamService.java](../demo/src/main/java/com/pax/android/demoapp/DownloadParamService.java)

    protected void onHandleIntent(@Nullable Intent intent) {
        //Specifies the download path for the parameter file, you can replace the path to your app's internal storage for security.
        final String saveFilePath = getFilesDir() + "YourPath";
        //todo Call this method to download into your specific directory, you can add some log here to monitor
        DownloadResultObject downloadResult = null;
        try {
            downloadResult = StoreSdk.getInstance().paramApi().downloadParamToPath(getApplication().getPackageName(), BuildConfig.VERSION_CODE, saveFilePath);
        } catch (NotInitException e) {
            Log.e(TAG, "e:" + e);
        }

        //businesscode==0, means download successful, if not equal to 0, please check the return message when need.
        if(downloadResult != null && downloadResult.getBusinessCode()==0){
            Log.i("downloadParamToPath: ", "download successful");
            //file download to saveFilePath above.
            //todo can start to add your logic.
        }else{
            //todo check the Error Code and Error Message for fail reason
            Log.e("downloadParamToPath: ", "ErrorCode: "+downloadResult.getBusinessCode()+"ErrorMessage: "+downloadResult.getMessage());
        }
    }   


## Template
The **parameter template file** used in **demo** is under folder assets/param_template.xml.
