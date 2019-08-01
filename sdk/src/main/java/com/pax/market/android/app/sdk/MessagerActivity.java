package com.pax.market.android.app.sdk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MessagerActivity extends Activity {
    private boolean mBond;
    private Messenger serverMessenger;
    private MyConn conn;
    private Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), msg.arg1 + "adg", Toast.LENGTH_SHORT).show();
            super.handleMessage(msg);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_messager);
        //绑定服务
        Intent intent = new Intent();
        intent.setAction("com.zixue.god.myapplication.server");
        conn = new MyConn();
        bindService(intent, conn, BIND_AUTO_CREATE);
        Button button = (Button) findViewById(R.id.bt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message clientMessage = Message.obtain();
                clientMessage.what = 1;
                clientMessage.arg1 = 12345;
                try {
                    clientMessage.replyTo = mMessenger;
                    serverMessenger.send(clientMessage);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mBond) {
            unbindService(conn);
        }
        super.onDestroy();
    }

    private class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //连接成功
            serverMessenger = new Messenger(service);
            Log.i("Main", "服务连接成功");
            mBond = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serverMessenger = null;
            mBond = false;
        }
    }

}