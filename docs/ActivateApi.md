# ActivateApi

com.pax.market.api.sdk.java.api.activate.ActivateApi, extends BaseApi

### The Constructor of ActivateApi

```
   public ActivateApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
        super(baseUrl, appKey, appSecret, terminalSN);
    }
```

### Activate terminal by TID

```
// api
  public SdkObject initByTID(String tid) {...}
// usage
Thread thread =  new Thread(new Runnable() {
    @Override
    public void run() {
        try {
            final SdkObject sdkObject = StoreSdk.getInstance().activateApi().initByTID("input tid");
            if (sdkObject.getBusinessCode() == 0) {
                //success
            } else {
                //failed
            }
        } catch (NotInitException e) {
            e.printStackTrace();
        }
    }
}) ;

thread.start();
```

| Parameter   | Type   | Description             |
| ----------- | ------ | ----------------------- |
| tid | String    | The tid to activate the terminal |

