package com.pax.android.demoapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pax.market.android.app.sdk.AdvertisementDialog;
import com.pax.market.android.app.sdk.BaseApiService;
import com.pax.market.android.app.sdk.LocationService;
import com.pax.market.android.app.sdk.StoreSdk;
import com.pax.market.android.app.sdk.dto.LocationInfo;
import com.pax.market.android.app.sdk.dto.OnlineStatusInfo;
import com.pax.market.android.app.sdk.dto.TerminalInfo;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.UpdateObject;
import com.pax.market.api.sdk.java.base.exception.NotInitException;

import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView bannerTitleTV;
    private TextView bannerTextTV;
    private TextView bannerSubTextTV;
    private TextView versionTV;
    private LinearLayout openClientlayout, checkUpdate;
    private MsgReceiver msgReceiver;
    private Switch tradingStateSwitch;
    private Button getTerminalInfoBtn;

    private ListViewForScrollView detailListView;
    private ScrollView scrollView;
    private DemoListViewAdapter demoListViewAdapter;
    private SPUtil spUtil;

    private LinearLayout nodataLayout;
    private List<Map<String, Object>> datalist;
    private static Handler handler = new Handler();

    private LinearLayout lvRetrieveData, openDownloadList;
    private ImageView mImgArrow;
    private LinearLayout lvChildRetrieve;
    private Button getTerminalLocation, getOnlineStatus; // todo remove


    private boolean isExpanded;
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
        checkUpdate = (LinearLayout) findViewById(R.id.check_update);
        versionTV = (TextView)findViewById(R.id.versionText);

        openDownloadList = (LinearLayout) findViewById(R.id.open_downloadlist_page);
        lvRetrieveData = (LinearLayout) findViewById(R.id.lv_retrieve_data);
        lvChildRetrieve = (LinearLayout) findViewById(R.id.lv_childs_retrieve);
        mImgArrow = (ImageView) findViewById(R.id.img_retrieve_data);
        getTerminalLocation = (Button) findViewById(R.id.get_location);

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

        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if update available from PAXSTORE.

                Thread thread =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final UpdateObject updateObject = StoreSdk.getInstance().updateApi().checkUpdate(BuildConfig.VERSION_CODE, getPackageName());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (updateObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                                        if (updateObject.isUpdateAvailable()) {
                                            Toast.makeText(MainActivity.this, "Update is available", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "No Update available", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "errmsg:>>" + updateObject.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.w("MessagerActivity", "updateObject.getBusinessCode():"
                                                + updateObject.getBusinessCode() + "\n msg:" + updateObject.getMessage());
                                    }
                                }
                            });

                        } catch (NotInitException e) {
                            Log.e(TAG, "e:" + e);
                        }
                    }
                }) ;

                thread.start();

            }
        });

        //open paxtore client
        openClientlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //put app 'NeptuneService' package name here for demo.
                //if the market don't have this app, it will show app not found, else will go to detail page in PAXSTORE market
                StoreSdk.getInstance().openAppDetailPage(getPackageName(), getApplicationContext());

            }
        });

        openDownloadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreSdk.getInstance().openDownloadListPage(getPackageName(), getApplicationContext());
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
                        Log.i("onSuccess: ",terminalInfo.toString());
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

        lvRetrieveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    isExpanded = false;
                    mImgArrow.setImageResource(R.mipmap.list_btn_arrow);
                    lvChildRetrieve.setVisibility(View.GONE);
                } else {
                    isExpanded = true;
                    mImgArrow.setImageResource(R.mipmap.list_btn_arrow_down);
                    lvChildRetrieve.setVisibility(View.VISIBLE);
                }
            }
        });

        getTerminalLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreSdk.getInstance().startLocate(getApplicationContext(), new LocationService.LocationCallback() {
                    @Override
                    public void locationResponse(LocationInfo locationInfo) {
                        Log.d(TAG, "Get Location Result：" + locationInfo.toString());
                        Toast.makeText(MainActivity.this,
                                "Get Location Result：" + locationInfo.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        getOnlineStatus = (Button) findViewById(R.id.get_online_status);
        getOnlineStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnlineStatusInfo onlineStatusFromPAXSTORE = StoreSdk.getInstance().getOnlineStatusFromPAXSTORE(getApplicationContext());
                Toast.makeText(MainActivity.this, onlineStatusFromPAXSTORE.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        scrollView = findViewById(R.id.root);
        scrollView.smoothScrollTo(0, 0);

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
                default:
                    break;
            }

        }
    }

    private void showAd(Context context) {
        int showResult = AdvertisementDialog.show(context, new AdvertisementDialog.OnLinkClick() {
            @Override
            public void onLinkClick(String linkUrl) {
                clickedLink = true;
            }
        });
        Log.d(TAG, "showResult:" + showResult);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onrestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onstart");
        if (!clickedLink) { // do not show ad after click link.
            showAd(this);
        } else {
            clickedLink = false;
        }
    }

    boolean clickedLink = false;

}
