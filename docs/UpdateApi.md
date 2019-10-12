# UpdateApi

com.pax.market.api.sdk.java.api.update.UpdateApi, extends BaseApi

### The Constructor of UpdateApi

```
public UpdateApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
    super(baseUrl, appKey, appSecret, terminalSN);
}
```

### Check if app has update on PAXSTORE

```
// api
public UpdateObject checkUpdate(int versionCode, String packageName) {...}
// usage
Thread thread =  new Thread(new Runnable() {
    @Override
    public void run() {
        try {
            final UpdateObject updateObject = StoreSdk.getInstance().updateApi().checkUpdate(BuildConfig.VERSION_CODE, getPackageName());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (updateObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                        if (updateObject.isUpdateAvailable()) {
                            Toast.makeText(MainActivity.this, "Update is available", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "No Update available", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.w("MessagerActivity", "updateObject.getBusinessCode():"
                              + updateObject.getBusinessCode() + "\n msg:" + updateObject.getMessage());
                    }
                }
            });

        } catch (NotInitException e) {
            e.printStackTrace();
        }
    }
}) ;

thread.start();
```

| Parameter   | Type   | Description             |
| ----------- | ------ | ----------------------- |
| versionCode | int    | The version code of app |
| packageName | String | The package name of app |

