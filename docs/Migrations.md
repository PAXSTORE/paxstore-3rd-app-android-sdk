# Migrations

### Migration to 8.0.1
<font color=#ff8c00>**Notice: Jcenter will not provide free download for our old sdks in 2022 , so we moved our latest sdk to Jitpack center, please update your gradle to integrate with our latest sdk.**
</font>
Add it in your root build.gradle at the end of repositories:


	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

 Add the dependency

```
implementation 'com.github.PAXSTORE:paxstore-3rd-app-android-sdk:8.0.1'
```

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



