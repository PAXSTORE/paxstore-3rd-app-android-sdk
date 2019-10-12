# ParamAPi

com.pax.market.api.sdk.java.api.param.ParamApi, extends BaseApi

### Constructors of ParamApi

```
public ParamApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
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


### Get terminal params to download

```
public ParamListObject getParamDownloadList(String packageName, int versionCode)
```

| Parameter   | Type   | Description          |
| ----------- | ------ | -------------------- |
| packageName | String | The app package name |
| versionCode | int    | The app version code |

**com.pax.market.api.sdk.java.base.dto.ParamListObject**

The push tasks detail infomation list, extends SdkObject

| Property   | Type              | Description                               |
| ---------- | ----------------- | ----------------------------------------- |
| totalCount | int               | The count of push task                    |
| list       | List<ParamObject> | The list of push tasks detail information |

**com.pax.market.api.sdk.java.base.dto.ParamObject**

The push task detail information, extends SdkObject

| Property       | Type   | Description                     |
| -------------- | ------ | ------------------------------- |
| actionId       | long   | The id of push task             |
| appId          | long   | The app id                      |
| versionCode    | int    | The app verison code            |
| downloadUrl    | String | The download url                |
| paramVariables | String | The param variables json string |
| md             | String | The file md5                    |

**com.pax.market.api.sdk.java.base.dto.SdkObject**

The base object

| Property     | Type   | Description                              |
| ------------ | ------ | ---------------------------------------- |
| businessCode | int    | The reuslt code, the default value is -1 |
| message      | String | The result message                       |

### Download param files

```
public DownloadResultObject downloadParamFileOnly(ParamObject paramObject, String saveFilePath)
```

| Parameter    | Type        | Description                                                  |
| ------------ | ----------- | ------------------------------------------------------------ |
| paramObject  | ParamObject | The ParamObject, you can get ParamObject from getParamDownloadListI() |
| saveFilePath | String      | The path that param files will be saved                      |

**com.pax.market.api.sdk.java.base.dto.DownloadResultObject**

| Property      | Type   | Description                    |
| ------------- | ------ | ------------------------------ |
| paramSavePath | String | The path that param file saved |

### Update push task status

```
public SdkObject updateDownloadStatus(String actionId, int status, int errorCode, String remarks)
```

| Parameter | Type   | Description                                                 |
| --------- | ------ | ----------------------------------------------------------- |
| actionId  | String | The id of push task                                         |
| status    | int    | The status of push task, 1:pending, 2:success, 3:fail       |
| errorCode | int    | The error code of push task, 0:Node error, 1:Download error |
| remarks   | String | The remarks of push task                                    |

### Update push task result in a batch

```
public SdkObject updateDownloadStatusBatch(List<UpdateActionObject> updateActionObjectList)
```

| Parameter              | Type                     | Description                    |
| ---------------------- | ------------------------ | ------------------------------ |
| updateActionObjectList | List<UpdateActionObject> | The list of UpdateActionObject |

**com.pax.market.api.sdk.java.base.dto.UpdateActionObject**

| Property  | Type   | Description                 |
| --------- | ------ | --------------------------- |
| actionId  | Long   | The id of push task         |
| status    | int    | The status of push task     |
| errorCode | int    | The error code of push task |
| remarks   | String | The remarks of push task    |

### Download param files to specific folder

```
public DownloadResultObject downloadParamToPath(String packageName, int versionCode, String saveFilePath)
```

| Parameter    | Type   | Description                             |
| ------------ | ------ | --------------------------------------- |
| packageName  | String | The package name                        |
| versionCode  | String | The version code                        |
| savaFilePath | String | The path that param files will be saved |

### Parse the downloaded parameter xml file to HashMap

Parse the downloaded parameter xml file, convert the xml elements to HashMap<String,String>, this method will not keep the xml fields order. HashMap will have a better performance.

```
// api
public HashMap<String,String> parseDownloadParamXml(File file) throws ParseXMLException {...}
// usage
HashMap<String, String> resultMap = StoreSdk.getInstance().paramApi().parseDownloadParamXml(parameterFile);
```

| Parameter | Type | Description             |
| --------- | ---- | ----------------------- |
| file      | File | The downloaded xml file |

### Parse the downloaded parameter xml file to LinkedHashMap

Parse the downloaded parameter xml file, convert the xml elements to LinkedHashMap<String,String>, this method will keep the xml fields order. LinkedHashMap performance is slower than HashMap.

```
public LinkedHashMap<String,String> parseDownloadParamXmlWithOrder(File file) throws ParseXMLException {...}
```
