### You can do partial download task with following new download api

### 1.sdk implementation

    implementation 'com.whatspos.sdk:paxstore-3rd-app-android-sdk:10.0.081501-SNAPSHOT'

### 2.init sdk

https://github.com/PAXSTORE/paxstore-3rd-app-android-sdk#step-1-get-application-key-and-secret

### 3.use new download api

Using the new API in the following way ensures that each background task downloads to its own separate folder,
preventing later background tasks from overwriting the files of earlier ones.

        DownloadConfig downloadConfig = new DownloadConfig.Builder().build();
        DownloadResultObject downloadResult = StoreSdk. getInstance(). paramApi(). 
                                executeDownload(getApplication().getPackageName(), Your app’s versionCode, saveFilePath, downloadConfig);

### 4.Get the task list for this download

DownloadResultObject .getDownloadedParamList()

### 5.The specific field description of this download task

public class **DownloadedObject**
Field Descriptions:

* **actionId**— The unique identifier for the download task.
* **path**— The path of the downloaded file.
* **isPartial**— A flag indicating whether the task is a partial parameter task or a full parameter task.

