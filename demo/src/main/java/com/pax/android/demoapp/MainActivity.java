package com.pax.android.demoapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pax.market.android.app.sdk.BaseApiService;
import com.pax.market.android.app.sdk.StoreSdk;
import com.pax.market.android.app.sdk.dto.TerminalInfo;

import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private TextView bannerTitleTV;
    private TextView bannerTextTV;
    private TextView bannerSubTextTV;
    private TextView versionTV;
    private LinearLayout openClientlayout;
    private MsgReceiver msgReceiver;
    private Switch tradingStateSwitch;
    private Button getTerminalInfoBtn;

    private ListViewForScrollView detailListView;
    private ScrollView scrollView;
    private DemoListViewAdapter demoListViewAdapter;
    private SPUtil spUtil;

    private LinearLayout nodataLayout;
    private List<Map<String, Object>> datalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        spUtil=new SPUtil();

        bannerTitleTV = (TextView) findViewById(R.id.banner_title);
        bannerTextTV = (TextView) findViewById(R.id.banner_text);
        bannerSubTextTV = (TextView) findViewById(R.id.banner_sub_text);
        tradingStateSwitch = (Switch) findViewById(R.id.tradingStateSwitch);
        openClientlayout = (LinearLayout) findViewById(R.id.openAppDetail);
        versionTV = (TextView)findViewById(R.id.versionText);

        versionTV.setText(getResources().getString(R.string.label_version_text)+" "+BuildConfig.VERSION_NAME);

        //receiver to get UI update.
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DemoConstants.UPDATE_VIEW_ACTION);
        registerReceiver(msgReceiver, intentFilter);

        //switch to set trading status.
        tradingStateSwitch.setChecked(((BaseApplication) getApplicationContext()).isReadyToUpdate());
        tradingStateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((BaseApplication) getApplicationContext()).setReadyToUpdate(true);
                } else {
                    ((BaseApplication) getApplicationContext()).setReadyToUpdate(false);
                }
            }
        });

        //open paxtore client
        openClientlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //put app 'NeptuneService' package name here for demo.
                //if the market don't have this app, it will show app not found, else will go to detail page in PAXSTORE market
                openAppDetail("com.pax.ipp.neptune");
            }
        });

        detailListView = findViewById(R.id.parameter_detail_list);
        nodataLayout = findViewById(R.id.nodata);

        String pushResultBannerTitle = spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TITLE);
        if(DemoConstants.DOWNLOAD_SUCCESS.equals(pushResultBannerTitle)){
            bannerTitleTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TITLE));
            bannerTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TEXT));
            bannerSubTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_SUBTEXT));

            datalist = spUtil.getDataList(DemoConstants.PUSH_RESULT_DETAIL);
            //if have push history, display it. the demo will only store the latest push record.
            if(datalist!=null && datalist.size() >0) {
                //display push history detail
                detailListView.setVisibility(View.VISIBLE);
                nodataLayout.setVisibility(View.GONE);
                demoListViewAdapter = new DemoListViewAdapter(this, datalist, R.layout.param_detail_list_item);
                detailListView.setAdapter(demoListViewAdapter);
            }else{
                //no data. check log for is a correct xml downloaded.
                detailListView.setVisibility(View.GONE);
                nodataLayout.setVisibility(View.VISIBLE);
                Toast.makeText(this, "File parse error.Please check the downloaded file.", Toast.LENGTH_SHORT).show();

            }
        }else {
            if(DemoConstants.DOWNLOAD_FAILED.equals(pushResultBannerTitle)) {
                bannerTitleTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TITLE));
                bannerTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TEXT));
            }
            //display as no data
            detailListView.setVisibility(View.GONE);
            nodataLayout.setVisibility(View.VISIBLE);
        }

        getTerminalInfoBtn = findViewById(R.id.GetTerminalInfo);

        getTerminalInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreSdk.getInstance().getBaseTerminalInfo(getApplicationContext(),new BaseApiService.ICallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        TerminalInfo terminalInfo = (TerminalInfo) obj;
                        Log.i("onSuccess2345: ",terminalInfo.toString());
                        Toast.makeText(getApplicationContext(), terminalInfo.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.i("onError: ",e.toString());
                        Toast.makeText(getApplicationContext(), "getTerminalInfo Error:"+e.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        scrollView = findViewById(R.id.root);
        scrollView.smoothScrollTo(0, 0);

    }

    private void openAppDetail(String packageName) {
        String url = String.format("market://detail?id=%s",packageName);
        Uri uri= Uri.parse(url);
        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
        intent.setClassName("com.pax.market.android.app", "com.pax.market.android.app.presentation.search.view.activity.SearchAppDetailActivity");
        intent.putExtra("app_packagename", packageName);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    /**
     * this method request sd card rw permission, you don't need this when you use internal storage
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {

        try {
            //check permissions
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // request permissions if don't have
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(msgReceiver);
        super.onDestroy();
    }

    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //update main page UI for push status
            int resultCode = intent.getIntExtra(DemoConstants.DOWNLOAD_RESULT_CODE, 0);
            switch (resultCode){
                case DemoConstants.DOWNLOAD_STATUS_SUCCESS:
                    bannerTitleTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TITLE));
                    bannerTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TEXT));
                    bannerSubTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_SUBTEXT));
                    datalist = spUtil.getDataList(DemoConstants.PUSH_RESULT_DETAIL);
                    if(datalist!=null && datalist.size() >0) {
                        //display push history detail
                        detailListView.setVisibility(View.VISIBLE);
                        nodataLayout.setVisibility(View.GONE);
                        demoListViewAdapter = new DemoListViewAdapter(MainActivity.this, datalist, R.layout.param_detail_list_item);
                        detailListView.setAdapter(demoListViewAdapter);
                    }else{
                        detailListView.setVisibility(View.GONE);
                        nodataLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "File parse error.Please check the downloaded file.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case DemoConstants.DOWNLOAD_STATUS_START:
                    bannerTitleTV.setText(DemoConstants.DOWNLOAD_START);
                    bannerTextTV.setText("Your push parameters are downloading");
                    break;
                case DemoConstants.DOWNLOAD_STATUS_FAILED:
                    bannerTitleTV.setText(DemoConstants.DOWNLOAD_FAILED);
                    bannerTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TEXT));
                    //display as no data
                    detailListView.setVisibility(View.GONE);
                    nodataLayout.setVisibility(View.VISIBLE);
                    break;
            }

        }
    }

}
