# SyncMsgTabApi

com.pax.market.api.sdk.java.api.sync.SyncMsgTabApi, extends BaseApi

### Constructors of SyncMsgTabApi

```java
 public SyncMsgTabApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
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


### Sync msg tab

```
public SdkObject syncMsgTab(List<String> tabNames, List<String> deleteTabNames)
```

| Parameter      | Type | Description            |
| -------------- | ---- | ---------------------- |
| tabNames       | List | The msg tabs to create |
| deleteTabNames | List | The msg tabs to delete |

**Create msg tab**

```
public SdkObject createMsgTab(List<String> tabNames)
```

| Parameter | Type | Description            |
| --------- | ---- | ---------------------- |
| tabNames  | List | The msg tabs to create |

**Delete msg ab**

```
public SdkObject deleteMsgTab(List<String> deleteTabNames) 
```

| Parameter | Type | Description |
| ------------ | ----------- | ------------------------------------------------------------ |
| deleteTabNames | List    | The msg tabs to delete                            |


