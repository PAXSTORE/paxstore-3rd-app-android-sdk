# PAXSTORE Install Inquirer Integration


By integrating with this function, you application can delay the upgrade.
When your application returns false to isReadyUpdate(), PAXSTORE client will try to upgrade your application next time.


### 1：Initialization of Sdk
Refer to the [SetUp](../README.md)

### 2: Update Inquirer
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
            initPaxStoreSdk();  //Initializing AppKey，AppSecret
        }

         private void initPaxStoreSdk() {
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
                      Toast.makeText(getApplicationContext(), "Cannot get API URL from PAXSTORE, Please install PAXSTORE first.", Toast.LENGTH_LONG).show();
                  }
              });
            }


         private void initInquirer() {
                //todo Init checking of whether app can be updated
                StoreSdk.initInquirer(new StoreSdk.Inquirer() {
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

