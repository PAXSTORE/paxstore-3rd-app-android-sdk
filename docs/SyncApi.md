# SyncApi

com.pax.market.api.sdk.java.api.sync.SyncApi, extends BaseApi

For how to use this api, please refer to [this](SyncAccessoryInfoIntegration.md)

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

