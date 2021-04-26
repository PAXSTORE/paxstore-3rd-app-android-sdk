# Migrations

### Migration to 8.0.1
No other efforts needed.

### Migration to 7.4.0
You are no longer required to get terminal serialNo by yourself to do the initialization.

    StoreSdk.getInstance().init(getApplicationContext(), appkey, appSecret, new BaseApiService.Callback() {
            @Override
            public void initSuccess() {
               //TODO Do your business here
            }

            @Override
            public void initFailed(RemoteException e) {
               //TODO Do failed logic here
                Toast.makeText(getApplicationContext(), "Cannot get API URL from PAXSTORE, Please install PAXSTORE first.", Toast.LENGTH_LONG).show();
            }
        });


### Migration to 7.2.0
Registing receiver is no longer required, just registering the IntentService will be enough.

Register your IntentService

    <service android:name=".DownloadParamService">
        <intent-filter>
            <action android:name="com.sdk.service.ACTION_TO_DOWNLOAD_PARAMS"/>
            <category android:name="${applicationId}"/>
        </intent-filter>
    </service>




