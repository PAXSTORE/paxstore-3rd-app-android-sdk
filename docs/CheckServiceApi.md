# CheckServiceApi

com.pax.market.api.sdk.java.api.check.CheckServiceApi, extends BaseApi

### The Constructor of CheckServiceApi

```
public CheckServiceApi(String baseUrl, String appKey, String appSecret, String terminalSN) {
    super(baseUrl, appKey, appSecret, terminalSN);
}
```

### Check if the service is available

```
// api
public ServiceAvailableObject checkServiceAvailable(ServiceType serviceType) {...}
// usage
new Thread(new Runnable() {
          @Override
          public void run() {
              ServiceAvailableObject serviceAvailableObject = null;
              try {
                            serviceAvailableObject = StoreSdk.getInstance().checkServiceApi().checkServiceAvailable(CheckServiceApi.ServiceType.LAUNCHER_UP);
                           if (serviceAvailableObject.getBusinessCode() == 0) {
                               Log.d(TAG, "serviceAvailableObject.isServiceAvailable():" + serviceAvailableObject.isServiceAvailable());
                           } else {
                               Log.d(TAG, "serviceAvailableObject.getBusinessCode():" + serviceAvailableObject.getBusinessCode());
                               Log.d(TAG, "serviceAvailableObject.getMessage():" + serviceAvailableObject.getMessage());
                           }
                        } catch (NotInitException e) {
                            Log.e(TAG, "e:" + e);
                        }
          }
      }).start();
```

| Parameter   | Type   | Description             |
| ----------- | ------ | ----------------------- |
| serviceType | ServiceType | The service that you want to know |

**Value of enum ServiceType**

| Value | Description |
|:---- |:----|
|LAUNCHER_UP|the CheckUp service|