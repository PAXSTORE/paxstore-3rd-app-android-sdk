# CheckServiceApi Background

Platform can inform the developer's solution application through the SDK that the user owes or pays off the debt, which corresponds to different states (unavailable/available), and the developer's application can control whether the solution application is available according to this state. Unavailable indicates that it takes effect from the first of next month.

### 1. Initialization of Sdk

Refer to the [SetUp](../README.md)

### 2. The Sample of CheckServiceApi

**// Api**
**public ServiceAvailableObject checkServiceAvailable(ServiceType serviceType) {...}**

~~~java
// usage
new Thread(new Runnable() {
    @Override
    public void run() {
    	ServiceAvailableObject serviceAvailableObject = null;
    	try {
            serviceAvailableObject = 		      StoreSdk.getInstance().checkServiceApi().checkServiceAvailable(CheckServiceApi.ServiceType.LAUNCHER_UP);
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
~~~

**// Api**
**public ServiceAvailableObject checkSolutionAppAvailable() {...}**

~~~java
// usage
Thread thread =  new Thread(new Runnable() {
      @Override
      public void run() {
          ServiceAvailableObject serviceAvailableObject = null;
          try {
              serviceAvailableObject = StoreSdk.getInstance().checkServiceApi().checkSolutionAppAvailable();
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
	}) ;
thread.start();
~~~

