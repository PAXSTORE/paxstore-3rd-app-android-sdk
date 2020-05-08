package com.pax.android.demoapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import java.util.ArrayList;
import java.util.List;


public class LauncherActivity extends FragmentActivity implements com.pax.android.demoapp.APIFragment.OnFragmentInteractionListener, com.pax.android.demoapp.GoFragment.OnFragmentInteractionListener, com.pax.android.demoapp.PushFragment.OnFragmentInteractionListener {
    private static final String TAG = LauncherActivity.class.getSimpleName();
    private ViewPager viewPager;
    private RadioGroup mGroup;
    private RadioButton mPush, mGo, mApi;
    private Fragment PushFragment, APIFragment, GoFragment;
    private ChartData chartData;


    public ChartData getChartData() {
        return chartData;
    }

    public void setChartData(ChartData chartData) {
        this.chartData = chartData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.laucnher);
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


    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        if(data!=null){
            chartData = (ChartData) data.getSerializableExtra("chart");
        }
    }



}
