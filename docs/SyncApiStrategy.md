# SyncApiStrategy

com.pax.market.api.sdk.java.api.sync.SyncApiStrategy, extends SyncApi，SyncApi extends BaseApi

For how to use this api, please refer to [this](SyncAccessoryInfoIntegration.md)

### The Constructor of  SyncApiStrategy

```
public SyncApiStrategy(String baseUrl, String appKey, String appSecret, String terminalSN) {
    super(baseUrl, appKey, appSecret, terminalSN);
}
```

### Synchronize terminal info

```
public SdkObject syncTerminalInfo(List<TerminalSyncInfo> infoList) {...}
```

| Parameter | Type                   | Description                    |
| --------- | ---------------------- | ------------------------------ |
| infoList  | List<TerminalSyncInfo> | The list of terminal sync info |

**com.pax.market.api.sdk.java.api.sync.dto.TerminalSyncInfo**

The terminal sync info, information about ancillary equipment

| Property    | Type   | Description        |
| ----------- | ------ | ------------------ |
| type        | int    | The sync type: 1: Application2: Device3: Hardware4: Application install history      |
| name        | String | Name of application/device/hardware               |
| version     | String | Version of application/device/hardware            |
| status      | String | Hardware Status             |
| remarks     | String | Remarks            |
| syncTime    | Long   | The synchronize time milliseconds   |
| installTime | Long   | Application install time milliseconds |
| fileSize    | Long   | File size of installed application        |
| fileType    | String | File type of installed application      |
| source      | String | The application installation mode(E.g. Remote or Local)             |
| hostModel   | String | The terminal host model(E.g. E500) |
| hostSN      | String | The terminal host SN    |

### Get locate info

**The geolocation service is not complete accuracy. Please refrain from incorporating the API into any app business scenarios.**

```java
public LocationObject getLocate() {}
```

### Permissions that need to be declared

```java
<uses-permission android:name="com.market.android.app.API_LOCATION" />
```

**com.pax.market.api.sdk.java.api.sync.dto.LocationObject**

The terminal location info, the structure shows below.

| Property       | Type   | Description                    |
| -------------- | ------ | ------------------------------ |
| longitude      | String | The longitude of location info |
| latitude       | String | The latitude of location info  |
| lastLocateTime | Long   | The last locate time           |

### Get  merchant info

```java
 public MerchantObject getMerchantInfo(){}
```

### Permissions that need to be declared

```java
 <uses-permission android:name="com.market.android.app.API_MERCHANT_INFO" />
```

**com.pax.market.api.sdk.java.api.sync.dto.MerchantObject**

The terminal merchant info, the structure shows below.

| Property | Type   | Description          |
| -------- | ------ | -------------------- |
| name     | String | The name of merchant |
