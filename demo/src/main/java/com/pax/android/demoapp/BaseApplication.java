package com.pax.android.demoapp;

import android.app.Application;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.pax.market.android.app.sdk.BaseApiService;
import com.pax.market.android.app.sdk.Notifications;
import com.pax.market.android.app.sdk.StoreSdk;

import net.grandcentrix.tray.AppPreferences;

/**
 * Created by fojut on 2017/8/24.
 */

public class BaseApplication extends Application {

    private static final String TAG = BaseApplication.class.getSimpleName();

    private boolean isReadyToUpdate = true;

    //todo make sure to replace with your own app's appkey and appsecret
    private static final String appkey = "DQOSMMGSBDCNIXOFZR1U";
    private static final String appSecret = "PMKNAMTBLBGZ2ARS110YQ7LCGT7UCU0NPO74ESS4";
    //todo please make sure get the correct SN here, for pax device you can integrate NeptuneLite SDK to get the correct SN
    private String SN = Build.SERIAL;
    public static AppPreferences appPreferences;


    @Override
    public void onCreate() {
        super.onCreate();
        //initial the SDK
        initPaxStoreSdk();
        appPreferences = new AppPreferences(getApplicationContext()); // this Preference comes for free from the library

    }

    private void initPaxStoreSdk() {
        //todo 1. Init AppKey，AppSecret and SN, make sure the appkey and appSecret is corret.
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
        //if you want the sdk to show notifications for you, initialize the Notifications
        Notifications.I.init(getApplicationContext())
                .setSmallIcon(R.drawable.logo_demo_white)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo_demo));

        //if you want to customize the notification, disable the Notifications we provided through below code.
        // Notifications.I.setEnabled(false);
    }


    private void initInquirer() {
        //todo 2. Init checking of whether app can be updated

        StoreSdk.getInstance().initInquirer(new StoreSdk.Inquirer() {
            @Override
            public boolean isReadyUpdate() {
                Log.i(TAG, "call business function....isReadyUpdate = " + isReadyToUpdate);
                //todo call your business function here while is ready to update or not
                return isReadyToUpdate;
            }
        });
    }

    public boolean isReadyToUpdate() {
        return isReadyToUpdate;
    }

    public void setReadyToUpdate(boolean readyToUpdate) {
        isReadyToUpdate = readyToUpdate;
        if(isReadyToUpdate){
            Toast.makeText(getApplicationContext(), getString(R.string.label_ready_to_update), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.label_not_ready_to_update), Toast.LENGTH_SHORT).show();
        }
    }
}
