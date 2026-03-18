# PAXSTORE GoInsight Associate Integration

By integrating with this function, developers can upload bizdata with device/app associate data to our GoInsight platform.


### 1.Add the dependency

```
    implementation 'com.whatspos.sdk:paxstore-3rd-app-android-goinsight-sdk:1.0.0'
```

### 2.Request Required Permissions
Before collecting device or app data, please request the required permissions first.

```java
    DeviceInfoFacade deviceInfoFacade = new DeviceInfoFacade(getApplicationContext());
    if (!deviceInfoFacade.hasAllRequiredPermissions(this)) {
        deviceInfoFacade.requestAllRequiredPermissions(this, 1001);
    }
```

You can also get all required permissions through below api:

```java
    List<String> permissions = deviceInfoFacade.getRequiredPermissions();
```

### 3.Customize BasicIngestionKey Value
If you do not want to use the default implementation in DeviceInfoProvider, you can register custom value providers for any built-in BasicIngestionKey.

```java
    DeviceInfoFacade deviceInfoFacade = new DeviceInfoFacade(getApplicationContext());

    deviceInfoFacade.registerBasicIngestionValueProvider(
            BasicIngestionKey.ASS_NETWORK_IP,
            provider -> "10.132.152.218,172.18.31.115"
    );

    deviceInfoFacade.registerBasicIngestionValueProvider(
            BasicIngestionKey.ASS_LOCATION,
            provider -> "31.230416,121.473701"
    );

    deviceInfoFacade.registerBasicIngestionValueProvider(
            BasicIngestionKey.ASS_ANDROID_VERSION,
            provider -> "Custom-Android-Version"
    );
```

Remove one custom implementation:

```java
    deviceInfoFacade.unregisterBasicIngestionValueProvider(BasicIngestionKey.ASS_LOCATION);
```

Clear all custom implementations:

```java
    deviceInfoFacade.clearBasicIngestionValueProviders();
```

### 4.Get Dataset Associate Config Sample
Use below api to fetch the associate config from server and update local cache.

        DatasetAssociateColsResponse config = StoreSdkExtention.getInstance().goInsightAssociateApi().getDatasetAssociateConfig();
        if (config != null) {
            // handle config here
        }

### 5.Upload BizData With Device Info Sample
_**:red_square::red_square::red_square: Attention: If the upload fails, you need to apply your own strategy to upload it again.**_

        // You have to create dataset in GoInsight, and each key you uploaded should exist in dataset.
        // syncTerminalBizDataWithDeviceInfo(...) will:
        // 1. load or fetch associate config
        // 2. collect device/app data based on the config
        // 3. merge bizdata and associate data
        // 4. upload merged data to GoInsight
        public void uploadToGoInsightAssociate() {
                GoInsightAssociateApi associateApi = StoreSdkExtention.getInstance().goInsightAssociateApi()
    
                List<Map<String, Object>> list = new ArrayList<>();
                Map<String, Object> map = new HashMap<>();
                map.put("product", "Paper");
                map.put("quantity", 1);
                map.put("sales", 4.98);
                list.add(map);
    
                SdkObject sdkObject = associateApi.syncTerminalBizDataWithDeviceInfo(list);
                if (sdkObject.getBusinessCode() == 0) {
                    // handle success logic
                } else {
                    // please try uploading the data again using your own strategy.
                    // We strongly recommend that you address the issue of data upload failure,
                    // which may occur due to network or other issues, to prevent you from losing important data.
                }
        }