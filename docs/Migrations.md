# Migrations

## Migration to 10.0.2

## Migration Summary

| Item                    | Requirement                  | Risk if Ignored                                 |
|-------------------------|------------------------------|------------------------------------------------|
| compileSdk              | Upgrade to 34                | Build Error: Manifest merger failed             |
| Notification Permission | Request at runtime           | Data Loss: Tasks terminated by system           |
| Service Start Logic     | Add `dataSync` service type  | Crash: `MissingForegroundServiceTypeException` |

### SDK Migration Guide: Android 14 (API 34) Support

This document provides instructions for migrating to the latest version of the SDK.
This update ensures compatibility with **Android 14 (API 34)**, specifically regarding **Foreground Service management** and **system permissions**.


## 1. Build Configuration Update

To support Android 14 requirements, you must update your project's **compileSdk** version.

### Action
- Update `compileSdk` to **34**.

### Reason
- The SDK now includes Android 14–specific declarations (for example, `dataSync` service type).
- Older `compileSdk` versions will **not recognize these constants**, leading to build failures.

### Note
- Your `targetSdkVersion` **does not need** to be changed to 34 immediately.

### Gradle Example

```gradle
android {
    compileSdk 34  // Required for compatibility

    defaultConfig {
        // targetSdk can remain at your current version (e.g., 31)
        targetSdk 31
    }
}
```


## 2. Mandatory Notification Permission

Starting with **Android 13 (API 33)**, users must explicitly grant notification permission.

### Requirement
- Your app **must dynamically request** `POST_NOTIFICATIONS` at runtime.

### Why This Is Required
- The SDK relies on **Foreground Services** to prevent background tasks (such as downloads) from being killed by the system.
- Without notification permission:
  - Foreground Services may be restricted
  - Background tasks may fail or be terminated

### Java Implementation Example

```java
if (Build.VERSION.SDK_INT >= 33) {
    if (ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

        ActivityCompat.requestPermissions(
            this,
            new String[]{Manifest.permission.POST_NOTIFICATIONS},
            1001
        );
    }
}
```

## 3. Foreground Service Start Logic (Android 14+)

If you directly invoke or manage the SDK's `Service`, you must specify the **service type** when calling `startForeground` on Android 14 and above.

### Action
- Add a version check.
- Include `FOREGROUND_SERVICE_TYPE_DATA_SYNC` when running on Android 14+.

### Java Implementation Example

```java
if (Build.VERSION.SDK_INT >= 34) { // Android 14+
    startForeground(
        NOTIFICATION_ID,
        notification,
        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
    );
} else {
    startForeground(NOTIFICATION_ID, notification);
}
```


- Update the AndroidManifest to add the required foregroundServiceType and exported attributes to your Service, ensuring it functions correctly and complies with system requirements.
```java
<service
    android:name=".service.DownloadParamService"
    android:foregroundServiceType="dataSync"
    android:exported="false">
    <intent-filter>
        <action android:name="com.sdk.service.ACTION_TO_DOWNLOAD_PARAMS"/>
        <category android:name="${applicationId}"/>
    </intent-filter>
</service>
```

---

### Migration to 8.7.0
When you encounter the error "duplicate class INotificationSideChannel", or you are using "com.android.support:appcompat-xxx", then it's time for you to migrate your project to AndroidX now. AndroidX replaces the original support library APIs with packages in the androidx namespace. Only the package and Maven artifact names changed; class, method, and field names did not change.

For faster and safer migration, you can refer to this [migration article](https://developer.android.com/jetpack/androidx/migrate) to get more details.

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
                Toast.makeText(getApplicationContext(), "Cannot get API URL from STORE client, Please install STORE client first.", Toast.LENGTH_LONG).show();
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



