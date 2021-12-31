# SyncMsgTagApi

com.pax.market.api.sdk.java.api.sync.SyncMsgTagApi, extends BaseApi

### Constructors of SyncMsgTagApi

```java
 public SyncMsgTagApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
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


### Sync msg tag

```
public SdkObject syncMsgTag(List<String> attachTagNames, List<String> detachTagNames)
```

| Parameter      | Type | Description            |
| -------------- | ---- | ---------------------- |
| attachTagNames | List | The msg tags to attach |
| detachTagNames | List | The msg tags to detach |

**Attach msg tag**

```
public SdkObject attachMsgTag(List<String> tagNames)
```

| Parameter | Type | Description            |
| --------- | ---- | ---------------------- |
| tagNames  | List | The msg tags to attach |

**Detach msg tag**

```
public SdkObject detachMsgTag(List<String> tagNames) 
```

| Parameter | Type | Description |
| ------------ | ----------- | ------------------------------------------------------------ |
| tagNames | List    | The msg tags to detach                     |

**Get all tag**

```
 public MsgTagObject getAllTag() {}
 
```

**com.pax.market.api.sdk.java.base.dto.MsgTagObject**

| Property | Type         | Description                              |
| -------- | ------------ | ---------------------------------------- |
| tags     | List<String> | The tags you attached to the application |

