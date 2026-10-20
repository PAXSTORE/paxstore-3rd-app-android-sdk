# PAXSTORE Install Inquirer Integration


By integrating with this function, you application can delay the upgrade.
When your application returns false to isReadyUpdate(), PAXSTORE client will try to upgrade your application next time.

:red_square: :red_square: :red_square: Please note: If your application does not initialize InstallInquirer successfully(especially when your app does not start before the upgrade), then InstallInquirer will not take effect, so your app will be upgraded directly.

:red_square: :red_square: :red_square: Please note: Please make sure the platform server version is v9.6.0 or higher, otherwise it will make the install inquirer function ineffective.



:red_square: :red_square: :red_square: **Please note: Since SDK v11.1.0, the RPCService declaration is NO LONGER merged into your app's manifest automatically, and the update inquirer APIs have been RENAMED from `initInquirer()` / `initInquirerOnly()` to `registerUpdateInquirer()` / `registerUpdateInquirerOnly()` (code calling the old APIs fails to compile after upgrading). If you want the update inquirer, you MUST declare the service in your own AndroidManifest.xml (see step 2 below), otherwise the update inquirer silently stops working after upgrading the SDK (an error is logged at runtime when the API is called, the app is NOT crashed). A build-time lint error (`PaxStoreRpcServiceNotRegistered`) will remind you if you forget the declaration.**

:red_square: :red_square: :red_square: **Please note: Update behavior change since SDK v11.1.0 for apps that do NOT use the update inquirer. In SDK versions before v11.1.0, such apps still carried the SDK-merged `RPCService`, which answered "ready to update" BY DEFAULT, so updates pushed from PAXSTORE were allowed by default — equivalent to the "force update" path of the flow chart (see step 4), skipping the foreground/background judgment. Since v11.1.0 this is fixed: apps that do not integrate the update inquirer no longer carry `RPCService`, and PAXSTORE client takes the "not integrated" branch of the flow chart — an update is only allowed while the app is IN THE BACKGROUND ("Update not allowed" while the app is in the foreground). Integrate the update inquirer (steps 2 and 3 below) if you want your app itself to decide whether it can be updated.**



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
- If you do NOT want the update inquirer, simply skip this step and do not call `registerUpdateInquirer()`. Your app then follows the "not integrated" branch of the flow chart in step 4: the update is only allowed while your app is in the background ("Update not allowed" while it is in the foreground). Note: in SDK versions before v11.1.0 such apps were updated directly by default, because the SDK-merged `RPCService` answered "ready to update" by default.
- Your app also needs the `android.permission.FOREGROUND_SERVICE` and `android.permission.FOREGROUND_SERVICE_DATA_SYNC` permissions (they are still merged from the SDK manifest).

### 3: Update Inquirer
Update inquirer: Your app will be asked whether it can be updated when there is a new version afther you
integrated this function.

Integrate with this function only need to call registerUpdateInquirer() after you init StoreSdk success.

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
                      registerUpdateInquirer();
                  }

                  @Override
                  public void initFailed(RemoteException e) {
                      Log.i(TAG, "initFailed: "+e.getMessage());
                      Toast.makeText(getApplicationContext(), "Cannot get API URL from STORE client, Please install STORE client first.", Toast.LENGTH_LONG).show();
                  }
              });
            }


         private void registerUpdateInquirer() {
                //todo Init checking of whether app can be updated
                StoreSdk.getInstance().registerUpdateInquirer(new StoreSdk.Inquirer() {
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

How the branches apply since SDK v11.1.0:

- **App integrated the update inquirer** (declared `RPCService` + called `registerUpdateInquirer()`): left branch — PAXSTORE client asks your app via `isReadyUpdate()`, "allow update" / "update not allowed" follows your return value.
- **App did NOT integrate the update inquirer** (neither calls `registerUpdateInquirer()` nor declares `RPCService` in its manifest): right branch — "allow update" only while the app is in the background, "Update not allowed" while it is in the foreground.
- Before v11.1.0, an app integrating the SDK but never calling `initInquirer()` still had the SDK-merged `RPCService` answering "ready to update" by default — the right branch above was never taken, and pushed updates were always allowed (equivalent to the "force update" path). v11.1.0 fixes this: an app must now call `registerUpdateInquirer()` and declare `RPCService` in its own manifest to enter the update-inquirer logic (the left branch above).
