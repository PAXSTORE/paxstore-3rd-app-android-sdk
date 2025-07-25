
# PAXSTORE 3rd App Android SDK

PAXSTORE 3rd App Android SDK provides simple and easy-to-use service interfaces for third party developers to develop android apps on PAXSTORE. The services currently include the following features:

1. [Downloading parameter](docs/DownloadIntegration.md)
2. [Inquirer update for 3rd party app](docs/InstallInquirerIntegration.md)
3. [CloudMessage](docs/CloudMsgIntegration.md)
4. [GoInsight](docs/GoInsightIntegration.md)

By using this SDK, developers can easily integrate with PAXSTORE. Please take care of your AppKey and AppSecret that generated by PAXSTORE system when you create an app.
<br>Refer to the following steps for integration.


## Please check below mirgrations.md if you are using previous sdk.
[Migrations](docs/Migrations.md)


## Requirements
**Android SDK version**
>SDK 19 or higher, depending on the terminal's paydroid version.

**Gradle's and Gradle plugin's version**
>Gradle version 4.1 or higher  
>Gradle plugin version 3.0.0+ or higher

## Download
Gradle:

<font color=#ff8c00>**Notice: Jcenter will not provide free download for our old sdks in 2022 , so we moved our latest sdk to Maven center, please update your gradle to integrate with our latest sdk.**
</font>

 Add the dependency

```
    implementation 'com.whatspos.sdk:paxstore-3rd-app-android-sdk:9.8.0'
```

##### Tips: In the near future, our platform will only support applications integrated with sdk version v8.7.0 or higher. Please upgrade the sdk to the latest version as soon as possible


## Permissions
PAXSTORE Android SDK need the following permissions, please add them in AndroidManifest.xml.

`<uses-permission android:name="android.permission.INTERNET" />`<br>


## ProGuard
The specific rules are [already bundled](https://github.com/PAXSTORE/paxstore-3rd-app-android-sdk/blob/master/sdk/proguard-rules.pro) into the aar, which can be interpreted automatically.

## Set Up

### Step 1: Get Application Key and Secret
Create a new app in PAXSTORE, and get **AppKey** and **AppSecret** from app detail page in developer center.

### Step 2: Initialization
Configuring the application element, edit AndroidManifest.xml, it will have an application element. You need to configure the android:name attribute to point to your Application class (put the full name with package if the application class package is not the same as manifest root element declared package)

    <application
        android:name=".BaseApplication"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme">

Initializing AppKey,AppSecret and SN
>Please note, make sure you have put your own app's AppKey and AppSecret correctly

    public class BaseApplication extends Application {
    
        private static final String TAG = BaseApplication.class.getSimpleName();
        
        //todo make sure to replace with your own app's appKey and appSecret
        private String appkey = "Your APPKEY";
        private String appSecret = "Your APPSECRET";
        
        @Override
        public void onCreate() {
            super.onCreate();
            initStoreSdk();
        }
        
        private void initStoreSdk() {
           //todo Init AppKey，AppSecret, make sure the appKey and appSecret is corret.
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
        }
    }

## More Apis

#### [StoreSdk](docs/StoreSdk.md)

#### [ParamApi](docs/ParamApiStrategy.md)

#### [SyncApiStrategy](docs/SyncApiStrategy.md)

#### [UpdateApi](docs/UpdateApi.md)

#### [ResultCode](docs/ResultCode.md)

#### [CloudMessageApi](docs/CloudMessageApi.md)

#### [CheckServiceApi](docs/CheckServiceApi.md)

## Migrating to Android 8.0
Since Android 8.0 has lots of changes that will affect your app's behavior, we recommand you to follow the guide to migrate
to Android 8.0. For further information, you can refer to https://developer.android.google.cn/about/versions/oreo/android-8.0-migration


## FAQ

#### 1. How to resolve dependencies conflict?

When dependencies conflict occur, the error message may like below:

    Program type already present: xxx.xxx.xxx

**Solution:**

You can use **exclude()** method to exclude the conflict dependencies by **group** or **module** or **both**.

e.g. To exclude 'com.google.code.gson:gson:2.8.5' in SDK, you can use below:

    implementation ('com.whatspos.sdk:paxstore-3rd-app-android-sdk:x.xx.xx'){
        exclude group: 'com.google.code.gson', module: 'gson'
    }

#### 2. How to resolve attribute conflict?

When attribute conflict occur, the error message may like below:

    Manifest merger failed : Attribute application@allowBackup value=(false) from 
    AndroidManifest.xml...
    is also present at [com.whatspos.sdk:paxstore-3rd-app-android-sdk:x.xx.xx] 
    AndroidManifest.xml...
    Suggestion: add 'tools:replace="android:allowBackup"' to <application> element
    at AndroidManifest.xml:..

**Solution:**

Add **xmlns:tools="http\://<span></span>schemas.android.com/tools"** in your manifest header

       <manifest xmlns:android="http://schemas.android.com/apk/res/android"
            package="com.yourpackage"
            xmlns:tools="http://schemas.android.com/tools">

Add **tools:replace = "the confilct attribute"** to your application tag:

        <application
            ...
            tools:replace="allowBackup"/>


More questions, please refer to [FAQ](https://github.com/PAXSTORE/paxstore-3rd-app-android-sdk/wiki/FAQ)

## License

See the [Apache 2.0 license](https://github.com/PAXSTORE/paxstore-3rd-app-android-sdk/blob/master/LICENSE) file for details.

    Copyright © 2019 Shenzhen Zolon Technology Co., Ltd. All Rights Reserved.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at following link.
    
         http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
