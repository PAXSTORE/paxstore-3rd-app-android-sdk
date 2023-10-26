# CheckServiceApi

com.pax.market.api.sdk.java.api.check.CheckServiceApi, extends BaseApi

### The Constructor of CheckServiceApi

```
public CheckServiceApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
    super(baseUrl, appKey, appSecret, terminalSN);
}
```

### Constructor parameters description

| Parameter  | Type   | Description     |
| ---------- | ------ | --------------- |
| baseUrl    | String | The base url    |
| appKey     | String | The app key     |
| appSecret  | String | The app secret  |
| terminalSN | String | The terminal SN |

### Check if the service is available

```
public ServiceAvailableObject checkServiceAvailable(ServiceType serviceType)
```

| Parameter   | Type   | Description             |
| ----------- | ------ | ----------------------- |
| serviceType | ServiceType | The service that you want to know |

**Value of enum ServiceType**

| Value | Description |
|:---- |:----|
|LAUNCHER_UP|the CheckUp service|

### Check if the Solution App is available

```
public ServiceAvailableObject checkSolutionAppAvailable()
```

**com.pax.market.api.sdk.java.base.dto.ServiceAvailableObject**

The ServiceAvailableObject extends SdkObject

| Property         | Type    | Description                                                  |
| ---------------- | ------- | ------------------------------------------------------------ |
| serviceAvailable | Boolean | The serviceAvailable indicates whether the application is available |