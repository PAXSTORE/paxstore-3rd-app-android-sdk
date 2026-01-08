package com.pax.android.demoapp.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.pax.android.demoapp.base.DemoConstants;
import com.pax.android.demoapp.R;
import com.pax.android.demoapp.adapter.FragAdapter;
import com.pax.market.android.app.sdk.msg.utils.AdvertisementDialog;

import java.util.ArrayList;
import java.util.List;


public class LauncherActivity extends FragmentActivity implements APIFragment.OnFragmentInteractionListener, PushFragment.OnFragmentInteractionListener {
    private static final String TAG = LauncherActivity.class.getSimpleName();
    private ViewPager viewPager;
    private RadioGroup mGroup;
    private RadioButton mPush, mApi;
    private Fragment PushFragment, APIFragment;
    private MsgReceiver msgReceiver;
    private List<FragmentReceiver> recivers = new ArrayList<>();
    private static Handler handler = new Handler();

    public static Handler getHandler() {
        return handler;
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);

        showAd(this);

        verifyStoragePermissions(this);

        //receiver to get UI update.
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DemoConstants.UPDATE_VIEW_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                    msgReceiver,
                    intentFilter,
                    Context.RECEIVER_NOT_EXPORTED
            );
        } else {
            registerReceiver(msgReceiver, intentFilter);
        }


        viewPager = findViewById(R.id.viewpager);
        mGroup = findViewById(R.id.r_group);
        mPush = findViewById(R.id.push);
        mPush.setChecked(true);
        mApi = findViewById(R.id.api);

        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.push:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.api:
                        viewPager.setCurrentItem(2);
                        break;
                }
            }
        });

        List<Fragment> fragments = new ArrayList<Fragment>();
        PushFragment = new PushFragment();
        APIFragment = new APIFragment();
        fragments.add(PushFragment);
        fragments.add(APIFragment);

        recivers.add((PushFragment)PushFragment);
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mPush.setChecked(true);
                        break;
                    case 2:
                        mApi.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkNotificationPermission();
        }, 1500);
    }

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    private void checkNotificationPermission() {
        // only above Android 13 (API 33) need to request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // request notification permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission permitted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission is denied. You will not receive any reminders.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};


    /**
     * this method request sd card rw permission, you don't need this when you use internal storage
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {

        try {
            //check permissions
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // request permissions if don't have
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class MsgReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            for (FragmentReceiver listener : recivers) {
                listener.onRecive(context, intent);
            }
        }
    }


    @Override
    protected void onDestroy() {
        recivers.clear();
        unregisterReceiver(msgReceiver);
        super.onDestroy();
    }



    private void showAd(Context context) {
        int showResult = AdvertisementDialog.show(context, new AdvertisementDialog.OnLinkClick() {
            @Override
            public void onLinkClick(String linkUrl) {

            }
        });
        Log.d(TAG, "showResult:" + showResult);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


}
