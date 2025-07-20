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

### Check the terminal status

```
public TerminalStatusObject checkTerminalStatus()
```

**com.pax.market.api.sdk.java.base.dto.TerminalStatusObject**

The TerminalStatusObject extends SdkObject

| Property    | Type   | Description                                                  |
| ----------- | ------ | ------------------------------------------------------------ |
| status      | String | The terminal Simplification status. For example "A".         |
| description | String | The terminal status detailed description. For example "Active". |

**Status  Result**

```java
PENDING("P", "Inactive"),
ACTIVE("A", "Active"),
SUSPEND("S", "Disabled"),
NOT_EXIST("N", "Not Exist"),
NO_PERM("NP", "No Permission");
```

### Check Solution Usage

This API is exclusively designed for Industry Solution-enabled ecosystems to synchronize and manage usage count data of industry solution.

```
public SdkObject checkSolutionUsage()
```

**com.pax.market.api.sdk.java.base.dto.SdkObject**

| Property     | Type   | Description                              |
| ------------ | ------ | ---------------------------------------- |
| businessCode | int    | The reuslt code, the default value is -1 |
| message      | String | The result message                       |

### Upload Solution Subscription Status

This API is exclusively designed for Industry Solution-enabled ecosystems to synchronize subscription status data of industry solution.

```
public SdkObject uploadSolutionSubStatus(boolean isSubscribe)
```

| Parameter   | Type    | Description                                                  |
| ----------- | ------- | ------------------------------------------------------------ |
| isSubscribe | boolean | It is used to synchronize subscription status of whether to subscribe(true) or not(false). |

**com.pax.market.api.sdk.java.base.dto.SdkObject**

| Property     | Type   | Description                              |
| ------------ | ------ | ---------------------------------------- |
| businessCode | int    | The reuslt code, the default value is -1 |
| message      | String | The result message                       |

