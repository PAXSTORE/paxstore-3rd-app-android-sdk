# PAXSTORE GoInsight Integration

By integrating with this function, developers can upload bizdata to our GoInsight platform.


### 1：Initialization of Sdk
Refer to the [SetUp](../README.md)

### 2.Add meta-data to AndroidManifest
       <!-- Add below meta-data to support GoInsight -->
       <meta-data android:name="PAXVAS_Insight"
                  android:value="true"/>

### 3.Upload BizData Sample

        // You have to create dataSet in GoInsight, and each key you uploaded should exists in dataSet.
        // You will find how to create dataSet in our GoInsight document.
        // Four kinds of value supported, including Text, Number, Date, and Image.
        // Image needed to be converted into BASE64-Encoded content and should be smaller than 30Kb.
        public void uploadToGoInsight() {
                List<Map<String, Object>> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", "Demo");
                    map.put("versionname", "com.pax.demo");
                    map.put("versionCode", i);
                    map.put("icon", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJAAAACQCAYAAADnRuK4AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA4ZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMTM4IDc5LjE1OTgyNCwgMjAxNi8wOS8xNC0wMTowOTowMSAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDpjZmY4MjQwNS0zYzgyLWY0NDYtYmYxOC0yM2QxNWU3OGZmZmUiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OUM1QjU5OUY1NjZGMTFFODlGQjlBMjQxNDhFM0ZEQUMiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OUM1QjU5OUU1NjZGMTFFODlGQjlBMjQxNDhFM0ZEQUMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTcgKE1hY2ludG9zaCkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo1ZDcyZTMxYS1hMTJhLTQxOWQtOWJiYi00MTBiZWI1ZWI0ZTkiIHN0UmVmOmRvY3VtZW50SUQ9ImFkb2JlOmRvY2lkOnBob3Rvc2hvcDoyYWJlMDgyNC0zNjM2LTExN2ItOGU3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");
                    map.put("tradeTime", System.currentTimeMillis());
                    list.add(map);
                }

                try {
                    SdkObject sdkObject = StoreSdk.getInstance().goInsightApi().syncTerminalBizData(list);
                    Log.e("GoInsightSample", "message: " + sdkObject.getMessage() + "  code: " + sdkObject.getBusinessCode());
                } catch (NotInitException e) {
                    Log.e("MainActivity", "e:" + e);
                }
            }
        }


### 4. Search APP BizData


The search app bizData API allow the third party system search data.<br/>
Note: This result of this API depends on the API query settings in GoInsight. Paging needs to be set when the query result set type is a details data.

**API**

"findTemrinalData" can search only this terminal's BizData, "findMerchantData" can search all BizData belongs to this merchant.
```
public DataQueryResultObject findTemrinalData(String queryCode)
public DataQueryResultObject findTemrinalData(String queryCode, TimestampRangeType rangeType)
public DataQueryResultObject findTemrinalData(String queryCode, Integer pageNo, Integer pageSize)
public DataQueryResultObject findMerchantData(String queryCode)
public DataQueryResultObject findMerchantData(String queryCode, TimestampRangeType rangeType)
public DataQueryResultObject findMerchantData(String queryCode, Integer pageNo, Integer pageSize)
public DataQueryResultObject findDataFromInsight(String queryCode, TimestampRangeType rangeType, Integer pageNo, Integer pageSize, boolean isMerchantAll)
```

**Input parameter(s) description**

| Name| Type | Nullable|Description |
|:---- | :----|:----|:----|
|queryCode|String|false|search by GoInsight api query code|
|rangeType|TimestampRangeType|true|you can choose the range of data results for search|
|pageNo|int|true|page number, value must >= 1|
|pageSize|int|true|the record number per page, range is 1 to 100 for details data query, range is 1 to 1000 for statistics data query|
|isMerchantAll|boolean|true|search single terminal bizdata or all bizdata belongs to this merchant|



Note: The pageNo param will be ignore when your query result set type is statistics chart.

Value of enum TimestampRangeType

| Value | Description |
|:---- |:----|
|P1D|Yesterday|
|P1W|Last Week|
|P1M|Last Month|
|P1Y|Last Year|
|R1D|Recent Day|
|R1W|Recent Week|
|R1M|Recent Month|
|R1Y|Recent Year|
|T1D|Today|
|T1W|This Week|
|T1M|This Month|
|T1Y|This Year|


**Sample codes**

```
  try {
            SdkObject sdkObject = StoreSdk.getInstance().goInsightApi().syncTerminalBizData(list);
        } catch (NotInitException e) {
            Log.e("MainActivity", "e:" + e);
        }
```

**Client side validation failed sample result(JSON formatted)**

```
{
	"businessCode": 36000,
	"message": ["The query code is not found"]
}
```

**Succssful sample result(JSON formatted)**

```
{
	"businessCode": 0,
	"data": {
        "worksheetName": "Merchant transaction amount trend (This Year)",
		"columns": [{
			"colName": "acquirer_type",
			"displayName": "Acquirer Type"
		}, {
			"colName": "currency",
			"displayName": "Currency"
		}, {
			"colName": "purchase_id",
			"displayName": "Purchase ID"
		}, {
			"colName": "amount",
			"displayName": "Amount"
		}, {
			"colName": "tax",
			"displayName": "Tax"
		}, {
			"colName": "_sys_marketid",
			"displayName": "Marketplace"
		}, {
			"colName": "_sys_merchantid",
			"displayName": "Merchant"
		}, {
			"colName": "_sys_terminalid",
			"displayName": "Terminal"
		}],
		"rows": [
			[{
				"colName": "acquirer_type",
				"value": "ZTO"
			}, {
				"colName": "currency",
				"value": "USD"
			}, {
                "colName": "purchase_id",
				"value": "15851195134847"
			}, {
                "colName": "amount",
				"value": "169.15"
			}, {
                "colName": "tax",
				"value": "64.38"
			}, {
				"colName": "_sys_marketid",
				"value": "demo"
			}, {
				"colName": "_sys_merchantid",
				"value": "Macy’s"
			}, {
				"colName": "_sys_terminalid",
				"value": "0820087295"
			}],
            [{
				"colName": "acquirer_type",
				"value": "ZTO"
			}, {
				"colName": "currency",
				"value": "USD"
			}, {
                "colName": "purchase_id",
				"value": "15851135975100"
			}, {
                "colName": "amount",
				"value": "2990.09"
			}, {
                "colName": "tax",
				"value": "64.12"
			}, {
				"colName": "_sys_marketid",
				"value": "demo"
			}, {
				"colName": "_sys_merchantid",
				"value": "Macy’s"
			}, {
				"colName": "_sys_terminalid",
				"value": "0820087295"
			}]
		],
        "offset": 10,
		"limit": 10,
		"hasNext": true,
	},
	"rateLimitRemain": ""
}
```

The type in dataSet of result is DataQueryResultObject. The structure shows below.

Structure of class TerminalDTO

|Property Name|Type|Description|
|:---|:---|:---|
|worksheetName|String|The result set worksheet name.|
|columns|List<Column>|The result set column.|
|rows|List<List<Row>>|The result set.|
|hasNext|Boolean|Is there any data.|
|offset|int|Rows offset if exit page info.|
|limit|int|Rows page size if exit page info.|

Structure of class Column

|Property Name|Type|Description|
|:---|:---|:---|
|colName|String|The dataset filed name in GoInsight|
|displayName|String|The dataset filed's display name|

Structure of class Row

|Property Name|Type|Description|
|:---|:---|:---|
|colName|String|The dataset filed name in GoInsight|
|value|Object|The dataset filed's value|

**Possible client validation errors**

> <font color=red>Parameter queryCode cannot be null</font>  
> <font color=red>Parameter queryCode length must is 8</font>  
> <font color=red>Parameter pageSize must be range is 1 to 1000</font>

**Possible business codes**

|Business Code|Message|Description|
|:---|:---|:---|
|36000|The query code is not found|&nbsp;|
|36001|The query code is not active|&nbsp;|
|36002|Query failed, please try again|&nbsp;|
|36003|The query is timeout, please try again|&nbsp;|
|36004|Insufficient permissions|&nbsp;|
|36005|Invalid pageNo|&nbsp;|
|36006|Invalid pageSize|&nbsp;|
|36008|Query failed, please contact administrator|&nbsp;|
|36009|Too many request, please try again later|&nbsp;

**Possible abnormal http codes**

|Http Code|Message|Description|
|:---|:---|:---|
|429|Too many request, please try again in one minute, two minutes or whatever|&nbsp;
