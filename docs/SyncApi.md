# SyncApi

com.pax.market.api.sdk.java.api.sync.SyncApi, extends BaseApi

### The Constructor of SyncApi

```
public SyncApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
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
| type        | int    | The sync type      |
| name        | String | Name               |
| version     | String | Version            |
| status      | String | Status             |
| remarks     | String | remarks            |
| syncTime    | Long   | The sync time      |
| installTime | Long   | The install time   |
| fileSize    | Long   | File size          |
| fileType    | String | File type          |
| source      | String | Source             |
| hostModel   | String | The terminal model |
| hostSN      | String | The terminal SN    |

### Synchronize terminal business data

```
public SdkObject syncTerminalBizData(List bizDataList) {...}
```

| Parameter   | Type | Description                       |
| ----------- | ---- | --------------------------------- |
| bizDataList | List | The list of business data to sync |