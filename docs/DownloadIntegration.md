# PAXSTORE Downloading Parameter Integration

By integrating with this function, admin can delivery parameters to the application.

### 1: Below two steps can save like 15 hours in average( Do not skip this!!!)
1. Check the appKey and appSecret that are the same with the web, and check again, and again, three times should be fine.
2. Check the apk you installed on the terminal matches the packageName and versionCode that you uploaded to PAXSTORE developer center,  and check again, and again.
3. Pay attention to the versionCode to the api downloadParamToPath(), the versionCode is your app's versionCode, do not input it with the sdk's versionCode!

Now you can go through below contents.

### 2：Initialization of Sdk
Refer to the [SetUp](../README.md)

### 3：Download Parameters API
`Notice: If you have integrated with our sdk before, you need to do some changes to migrate to our newest one.
Registing receiver is no more needed, just registering the IntentService will be enough.`

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
            downloadResult = StoreSdk.getInstance().paramApi().downloadParamToPath(getApplication().getPackageName(), "The versionCode of your app", saveFilePath);
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

Below codes are needed when your application target devices include Android8+.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationUtils.showForeGround(this, "Downloading params");
        return super.onStartCommand(intent, flags, startId);
    }

## Template
See [Template](../demo/src/main/assets/param_template.xml)
