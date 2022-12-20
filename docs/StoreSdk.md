
# StoreSdk
com.pax.market.android.app.sdk.StoreSdk

### Initialize StoreSdk

```
// Initialize StoreSdk api
public void init(final Context context, final String appKey, final String appSecret,
                 final BaseApiService.Callback callback) throws NullPointerException
```

| Parameter        | Type                    | Description    |
| ---------------- | ----------------------- | -------------- |
| context          | Context                 | Context        |
| appKey           | String                  | The app key    |
| appSecret        | String                  | The app secret |
| callback         | BaseApiService.Callback | Callback       |

**com.pax.market.android.app.sdk.BaseApiService**

BaseApiService, implements ProxyDelegate, the structure shows below

| Property   | Type              | Description                    |
| ---------- | ----------------- | ------------------------------ |
| instance   | BaseApiService    | The instance of BaseApiService |
| context    | Context           | Context                        |
| sp         | SharedPreferences | ShardPreferences               |
| storeProxy | StoreProxyInfo    | Store proxy info               |

**BaseApiService.Callback**

```
public interface Callback {
    void initSuccess();
    void initFailed(RemoteException e);
}
```

**com.pax.market.api.sdk.java.base.client.ProxyDelegate**

```
public interface ProxyDelegate {
    Proxy retrieveProxy();
    String retrieveProxyAuthorization();
}
```

**com.pax.market.android.app.sdk.dto.StoreProxyInfo**

The store proxy info, the structure shows below

| Property      | Type   | Description                          |
| ------------- | ------ | ------------------------------------ |
| type          | int    | Store proxy type, 0: DIRECT, 1: HTTP |
| host          | String | Store proxy host                     |
| port          | int    | Store proxy port                     |
| authroization | String | Store authroization                  |

### Get ParamApi instance

```
// Get ParamApi instance api
public ParamApi paramApi() throws NotInitException {...}
// usage
ParamApi paramApi = StoreSdk.getInstance().paramApi();
```

### ~~Get SyncApi instance~~  

SyncApi replaced by SyncApiStrategy

### Get SyncApiStrategy instance 

```
// Get SyncApiStrategy instance api
public SyncApiStrategy syncApi() throws NotInitException {...}
// usage
SyncApiStrategy syncApi = StoreSdk.getInstance().syncApi();
```

### Get UpdateApi instance

```
// Get UpdateApi instance api
public UpdateApi updateApi() throws NotInitException {...}
// usage
UpdateApi updateApi = StoreSdk.getInstance().updateApi();
```
### Get CloudMessageApi instance

```java
// Get CloudMessageApi instance api
public CloudMessageApi cloudMessageApi() throws NotInitException {...}
// usage
CloudMessageApi cloudMessageApi = StoreSdk.getInstance().cloudMessageApi();
```

### Check if initialized

```
// Check if initialized api
public boolean checkInitialization() {...}
// usage
boolean init = StoreSdk.getInstance().checkInitialization();
```

### Update inquirer

Store app will ask you before installing the new version of your app. Ignore this if you don't have Update inquirer requirement. You can implement com.pax.market.android.app.sdk.StoreSdk.Inquirer#isReadyUpdate() to tell Store App whether your app can be updated now.

```
// Update inquirer api
public void initInquirer(final Inquirer inquirer) {...}
```

### Initialize api directly

If you known the exact apiUrl, you can call this method to initialize ParamApi and SyncApi directly instead of calling method init().

```
// Initialize api directly api
public void initApi(String apiUrl, String appKey, String appSecret, String terminalSerialNo, ProxyDelegate proxyDelegate)
```

| Parameter        | Type          | Description      |
| ---------------- | ------------- | ---------------- |
| apiUrl           | String        | The exact apiUrl |
| appKey           | String        | The app key      |
| appSecret        | String        | The app secret   |
| terminalSerialNo | String        | The teminal SN   |
| proxyDelegate    | ProxyDelegate | ProxyDelegate    |

### Get Terminal Base Information

API to get base terminal information from PAXSTORE client. (Support from PAXSTORE client V6.1.)

    // Get terminal base information api
    public void getBaseTerminalInfo(Context context, BaseApiService.ICallBack callback) {...}
    
    // usage
    StoreSdk.getInstance().getBaseTerminalInfo(getApplicationContext(),new BaseApiService.ICallBack() {
        @Override
        public void onSuccess(Object obj) {
            TerminalInfo terminalInfo = (TerminalInfo) obj;
            Log.i("onSuccess: ",terminalInfo.toString());
        }
    
        @Override
        public void onError(Exception e) {
            Log.i("onError: ",e.toString());
        }
    });

### Sync and update PAXSTORE proxy information

```
// api
public void updateStoreProxyInfo(Context context, StoreProxyInfo storeProxyInfo) {...}
```
### Open app  detail page

Open your app detail page in PAXSTORE. If the market don't have this app, it will show app not found, else will go to detail page in PAXSTORE market

    // api
    public void openAppDetailPage(String packageName, Context context) {...}
    // usage
    StoreSdk.getInstance().openAppDetailPage(getPackageName(), getApplicationContext());

### Open PAXSTORE's download page

Open download page in PAXSTORE. You can see app's downloading progress in this page.

    // api
    public void openDownloadListPage(String packageName, Context context) {...}
    // usage
    StoreSdk.getInstance().openDownloadListPage(getPackageName(), getApplicationContext());

### Get PAXSTORE PUSH online status

```
public OnlineStatusInfo getOnlineStatusFromPAXSTORE(Context context) {...}
```

**com.pax.market.android.app.sdk.dto.OnlineStatusInfo**

The terminal online status info, the structure shows below

| Property | Type    | Description                            |
| -------- | ------- | -------------------------------------- |
| online   | boolean | The PAXSTORE online push online status |

### Get location from PAXSTORE.

    // Get location api
    public void startLocate(Context context, LocationService.LocationCallback locationCallback) {...}
    // usage
    StoreSdk.getInstance().startLocate(getApplicationContext(), new LocationService.LocationCallback() {
                        @Override
                        public void locationResponse(LocationInfo locationInfo) {
                            Log.d("MainActivity", "Get Location Result：" + locationInfo.toString());
                        }
                    });

Parse xml file. Only support when the xml is the type of HashMap.

      LinkedHashMap<String, String> resultMap = StoreSdk.getInstance().paramApi().parseDownloadParamJsonWithOrder(parameterFile);

Parse json file. Only support when the json is the type of HashMap.

      LinkedHashMap<String, String> resultMap = StoreSdk.getInstance().paramApi().parseDownloadParamXmlWithOrder(parameterFile);


### QueryResult

| code | message                     | Description                        |
| ---- | --------------------------- | ---------------------------------- |
| 0    | success                     | success                            |
| -1   | Get location failed         | Get location failed                |
| -2   | Init LocationManager failed | Init LocationManager failed        |
| -3   | Not allowed                 | Get info not allowed               |
| -4   | Get location too fast       | Get location too fast              |
| -5   | Push not enabled            | Push not enabled                   |
| -6   | Query failed                | Query from content provider failed |
| -10  | unknown                     | Unknown                            |

### Other object

**com.pax.market.android.app.sdk.dto.LocationInfo**

The terminal location info, the structure shows below.

| Property       | Type   | Description                    |
| -------------- | ------ | ------------------------------ |
| longitude      | String | The longitude of location info |
| latitude       | String | The latitude of location info  |
| lastLocateTime | Long   | The last locate time           |

**com.pax.market.android.app.sdk.dto.TerminalInfo**

The terminal info, the structure shows below

| Property             | Type       | Description                                         |
| -------------------- | ---------- | --------------------------------------------------- |
| tid                  | String     | The tid of terminal                                 |
| terminalName         | String     | The name of terminal                                |
| serialNo             | String     | The serial no of terminal                           |
| modelName            | String     | The modle name of terminal                          |
| factory              | String     | The manufactory of terminal                         |
| ~~merchantName~~~~~~ | ~~String~~ | ~~The merchant name of terminal~~                   |
| status               | String     | The online status of terminal, 0:online, -1:offline |

### MerchantName in TerminalInfo has been removed from version 8.7.0,  getMerchantInfo using   [SyncApiStrategy](SyncApiStrategy.md)



