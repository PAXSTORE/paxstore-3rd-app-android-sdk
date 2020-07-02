package com.pax.android.demoapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.pax.market.android.app.sdk.AdvertisementDialog;
import com.pax.market.android.app.sdk.StoreSdk;
import com.pax.market.api.sdk.java.base.dto.DataQueryResultObject;
import com.pax.market.api.sdk.java.base.exception.NotInitException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.pax.android.demoapp.GenerateDataActivity.trans_bar;
import static com.pax.android.demoapp.GenerateDataActivity.trans_line;
import static com.pax.android.demoapp.GenerateDataActivity.trans_pi;


public class LauncherActivity extends FragmentActivity implements com.pax.android.demoapp.APIFragment.OnFragmentInteractionListener, com.pax.android.demoapp.GoFragment.OnFragmentInteractionListener, com.pax.android.demoapp.PushFragment.OnFragmentInteractionListener {
    private static final String TAG = LauncherActivity.class.getSimpleName();
    private ViewPager viewPager;
    private RadioGroup mGroup;
    private RadioButton mPush, mGo, mApi;
    private Fragment PushFragment, APIFragment, GoFragment;
    private ChartData chartData_line, chartData_bar, chartData_pi;
    private Map<String,Boolean> flags = new ConcurrentHashMap<>();
    private SPUtil spUtil;
    private MsgReceiver msgReceiver;
    private List<F_Revicer> recivers = new ArrayList<>();
    private static Handler handler = new Handler();

    public static Handler getHandler() {
        return handler;
    }

    public ChartData getChartData_line() {
        return chartData_line;
    }


    public ChartData getChartData_bar() {
        return chartData_bar;
    }


    public ChartData getChartData_pi() {
        return chartData_pi;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laucnher);

        showAd(this);

        verifyStoragePermissions(this);
        spUtil = new SPUtil();

        //receiver to get UI update.
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DemoConstants.UPDATE_VIEW_ACTION);
        registerReceiver(msgReceiver, intentFilter);


        viewPager = findViewById(R.id.viewpager);
        mGroup = findViewById(R.id.r_group);
        mPush = findViewById(R.id.push);
        mPush.setChecked(true);
        mGo = findViewById(R.id.go);
        mApi = findViewById(R.id.api);

        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.push:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.go:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.api:
                        viewPager.setCurrentItem(2);
                        break;
                }
            }
        });

        List<Fragment> fragments = new ArrayList<Fragment>();
        PushFragment = new PushFragment();
        GoFragment = new GoFragment();
        APIFragment = new APIFragment();
        fragments.add(PushFragment);
        fragments.add(GoFragment);
        fragments.add(APIFragment);

        recivers.add((PushFragment)PushFragment);
        recivers.add((GoFragment)GoFragment);
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        //设定适配器
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
                    case 1:
                        mGo.setChecked(true);
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

        queryWrap();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        //解析返回数据更新UI
        if (data != null) {
            Bundle bundle = data.getBundleExtra("back");
            ChartData graph_line = (ChartData) bundle.getSerializable("data_line");
            ChartData graph_bar = (ChartData) bundle.getSerializable("data_bar");
            ChartData graph_pi = (ChartData) bundle.getSerializable("data_pi");
            chartData_line = graph_line;
            chartData_bar = graph_bar;
            chartData_pi = graph_pi;
        }
    }

    private void queryWrap() {
        queryBizData("k420he8f", ChartType.LINE);
        queryBizData("3pyv3ce3", ChartType.BAR);
        queryBizData("mutmmmpi", ChartType.PI);
    }

    private void queryBizData(final String queryCode, final ChartType type) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DataQueryResultObject temrinalData = StoreSdk.getInstance().goInsightApi().findMerchantData(queryCode);
                    Log.d(TAG, "msg::" + temrinalData.getMessage());
                    List<DataQueryResultObject.Column> columns = temrinalData.getColumns();
                    //transform to chartData
                    ChartData chart;
                    switch (type) {
                        case BAR:
                            chart = trans_bar(temrinalData);
                            chartData_bar = chart;
                            flags.put("BAR",true);
                            break;
                        case LINE:
                            chart = trans_line(temrinalData);
                            chartData_line = chart;
                            flags.put("LINE",true);
                            break;
                        case PI:
                            chart = trans_pi(temrinalData);
                            chartData_pi = chart;
                            flags.put("PI",true);
                            break;
                    }

                } catch (NotInitException e) {
                    e.printStackTrace();
                    flags.put("LINE",false);
                    flags.put("BAR",false);
                    flags.put("PI",false);
                }


                if (flags.size()==3){
                    for (F_Revicer listener : recivers) {
                        listener.notify_fragment(LauncherActivity.this, null);
                    }
                }
            }
        });
        thread.start();

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
            for (F_Revicer listener : recivers) {
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




}
