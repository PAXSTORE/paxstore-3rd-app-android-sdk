# PAXSTORE Install Inquirer Integration


By integrating with this function, you application can delay the upgrade.
When your application returns false to isReadyUpdate(), PAXSTORE client will try to upgrade your application next time.

:red_square: :red_square: :red_square: Please note: If your application does not initialize InstallInquirer successfully(especially when your app does not start before the upgrade), then InstallInquirer will not take effect, so your app will be upgraded directly.

:red_square: :red_square: :red_square: Please note: Please make sure the platform server version is v9.6.0 or higher, otherwise it will make the install inquirer function ineffective.



:red_square: :red_square: :red_square: Please note: Since SDK v11.1.0, the RPCService declaration is NO LONGER merged into your app's manifest automatically. If you want the update inquirer, you MUST declare the service in your own AndroidManifest.xml (see step 2 below), otherwise `initInquirer()` / `initInquirerOnly()` throws an `IllegalStateException` at runtime and your app will crash on startup. A build-time lint error (`PaxStoreRpcServiceNotRegistered`) will remind you if you forget the declaration, even if your project sets `lintOptions { abortOnError false }` the runtime crash still applies.



### 1：Initialization of Sdk
Refer to the [SetUp](../README.md)

### 2: Register RPCService in your AndroidManifest.xml
Since v11.1.0 the SDK no longer registers `RPCService` for you. Add the service declaration below inside the `<application>` element of YOUR AndroidManifest.xml to enable the update inquirer:

```xml
<service android:name="com.pax.market.android.app.sdk.RPCService"
    android:foregroundServiceType="dataSync"
    android:permission="com.market.android.app.sdk.INSTALL_INQUIRER"
    android:exported="true">
    <intent-filter>
        <action android:name="${applicationId}.ACTION_RPC_SERVICE" />
    </intent-filter>
</service>
```

Note:
- If you do NOT want the update inquirer, simply skip this step and do not call `initInquirer()`, then your app will be upgraded directly without being asked.
- Your app also needs the `android.permission.FOREGROUND_SERVICE` and `android.permission.FOREGROUND_SERVICE_DATA_SYNC` permissions (they are still merged from the SDK manifest).

### 3: Update Inquirer
Update inquirer: Your app will be asked whether it can be updated when there is a new version afther you
integrated this function.

Integrate with this function only need to call initInquirer() after you init StoreSdk success.

    public class BaseApplication extends Application {

        private static final String TAG = BaseApplication.class.getSimpleName();

        //todo make sure to replace with your own app's appKey and appSecret
        private String APP_KEY = "Your APPKEY";
        private String APP_SECRET = "Your APPSECRET";
        @Override
        public void onCreate() {
            super.onCreate();
            initStoreSdk();  //Initializing AppKey，AppSecret
        }

         private void initStoreSdk() {
                //todo Init AppKey，AppSecret, make sure the appKey and appSecret is corret.
              StoreSdk.getInstance().init(getApplicationContext(), appkey, appSecret, new BaseApiService.Callback() {
                  @Override
                  public void initSuccess() {
                      Log.i(TAG, "initSuccess.");
                      initInquirer();
                  }

                  @Override
                  public void initFailed(RemoteException e) {
                      Log.i(TAG, "initFailed: "+e.getMessage());
                      Toast.makeText(getApplicationContext(), "Cannot get API URL from STORE client, Please install STORE client first.", Toast.LENGTH_LONG).show();
                  }
              });
            }


         private void initInquirer() {
                //todo Init checking of whether app can be updated
                StoreSdk.getInstance().initInquirer(new StoreSdk.Inquirer() {
                    @Override
                    public boolean isReadyUpdate() {
                        Log.i(TAG, "call business function....isReadyUpdate = " + !isTrading());
                        //todo call your business function here while is ready to update or not
                        return !isTrading();
                    }
                });
         }

        //This is a sample of your business logic method
        public boolean isTrading(){
            return true;
        }
    }

### 4: Update Inquirer Flow Chart
![InstallInquirer logic](https://github.com/PAXSTORE/paxstore-3rd-app-android-sdk/blob/master/docs/images/InstallInquirerlogic.png)
