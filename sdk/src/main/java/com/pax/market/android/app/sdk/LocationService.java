package com.pax.market.android.app.sdk;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.pax.market.android.app.sdk.dto.LocationInfo;
import com.pax.market.android.app.sdk.util.NotificationUtils;

/**
 * Created by zcy on 2019/5/5 0005.
 */

public class LocationService extends Service {

    private static final int MSG_LOCATION_REQUEST = 0x01;
    private static final int MSG_LOCATION_RESPONSE = 0x02;
    private static final String LOCATION_ACTION = "com.pax.market.android.app.locationresponseservice";
    private static final String BIND_SERVICE_FAILED = "Bind service failed, PAXSTORE may not running or PAXSTORE client version is below 6.3. Please check";
    private static final String LOCATION_RESULT_KEY = "locationResult";
    private static final String PAXSTORE_PACKAGENAME = "com.pax.market.android.app";
    private static final int GET_LOCATION_FAILED = -1;
    static LocationCallback locationCallback;
    private boolean mBond;
    private Messenger serverMessenger;
    private MyConn conn;
    private Gson gson = new Gson();
    private Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOCATION_RESPONSE:
                    LocationInfo locationInfo = gson.fromJson((String) msg.getData().get(LOCATION_RESULT_KEY), LocationInfo.class);
                    if (locationCallback != null) {
                        locationCallback.locationResponse(locationInfo);
                    }
                    break;
                default:
                    break;
            }
            LocationService.this.stopSelf();
            super.handleMessage(msg);
        }
    });

    public static void setCallback(LocationCallback locationCallback) {
        LocationService.locationCallback = locationCallback;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationUtils.showForeGround(this, "LocationService");
        if (mBond) {
            Log.w("LocationService", "Already bound service");
            return super.onStartCommand(intent, flags, startId);
        }
        //绑定服务
        conn = new MyConn();
        Intent intent2 = new Intent();
        intent2.setAction(LOCATION_ACTION);
        intent2.setPackage(PAXSTORE_PACKAGENAME);
        boolean bindResult = bindService(intent2, conn, BIND_AUTO_CREATE);
        if (!bindResult) {
            LocationInfo locationInfo = new LocationInfo();
            locationInfo.setMessage(BIND_SERVICE_FAILED);
            locationInfo.setBusinessCode(GET_LOCATION_FAILED);
            locationCallback.locationResponse(locationInfo);
            this.stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mBond) {
            unbindService(conn);
        }
        locationCallback = null;
        super.onDestroy();
    }

    public interface LocationCallback {
        void locationResponse(LocationInfo locationInfo);
    }

    private class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接成功
            serverMessenger = new Messenger(service);
            Log.i("LocationService", "Location service connected");
            mBond = true;


            Message clientMessage = Message.obtain();
            clientMessage.what = MSG_LOCATION_REQUEST;
            try {
                clientMessage.replyTo = mMessenger;
                serverMessenger.send(clientMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
                LocationService.this.stopSelf();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serverMessenger = null;
            mBond = false;
            LocationService.this.stopSelf();
        }
    }

}
