# PAXSTORE Downloading Parameter Integration

By integrating with this function, admin can dilivery parameters to the application.
### 1：Initialization of Sdk
Refer to the [SetUp](../README.md)

### 2：Download Parameters API
Download parameter

Register your receiver (**Replace the category name with your own app package name**)

     <receiver android:name=".YourReceiver">
              <intent-filter>
                  <action android:name="com.paxmarket.ACTION_TO_DOWNLOAD_PARAMS" />
                  <category android:name="Your PackageName" />
              </intent-filter>
     </receiver>

Create your receiver. Since download will cost some time, we recommend you do it in your own service

      public class YourReceiver extends BroadcastReceiver {
          @Override
          public void onReceive(Context context, Intent intent) {
              //todo add log to see if the broadcast is received, if not, please check whether the bradcast config is correct
              Log.i("DownloadParamReceiver", "broadcast received");
              //since download may cost a long time, we recommend you do it in your own service
              context.startService(new Intent(context, YourService.class));
          }
      }
After you get broadcast, download params in your service

    public int onStartCommand(Intent intent, int flags, int startId) {
            //Specifies the download path for the parameter file, you can replace the path to your app's internal storage for security.
            final String saveFilePath = getFilesDir() + "YourPath";
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
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
            });
            thread.start();
            return super.onStartCommand(intent, flags, startId);
        }


## Template
The **parameter template file** used in **demo** is under folder assets/param_template.xml.
