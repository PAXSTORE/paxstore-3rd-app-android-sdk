package com.pax.android.demoapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.pax.android.demoapp.R;
import com.pax.android.demoapp.adapter.SalesAdapter;
import com.pax.android.demoapp.dto.SalesRecord;
import com.pax.android.demoapp.utils.DateUtils;
import com.pax.android.demoapp.utils.RandomCSVReader;
import com.pax.android.demoapp.utils.SPUtil;
import com.pax.market.android.app.sdk.StoreSdk;
import com.pax.market.api.sdk.java.base.dto.SdkObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoInsightActivity extends Activity {


    private static final String TAG = GoInsightActivity.class.getSimpleName();
    private static final String SP_SALES = "sp_sales";
    private static final String CSV_FILE_NAME = "sales_data.csv";
    SPUtil spUtil = new SPUtil();
    private RecyclerView recyclerView;
    private SalesAdapter adapter;
    private List<SalesRecord> SalesRecords = new ArrayList<>();
    private Button btnAdd;
    private LinearLayout btnBack;
    private int itemCounter = 1;
    private RandomCSVReader csvReader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goinsight);
        setupCSVReader();
        initData();
        initViews();
        setupRecyclerView();
    }

    private void initData() {

        SalesRecords = spUtil.getDataListByType(getApplicationContext(), SP_SALES, new TypeToken<List<SalesRecord>>() {
        });
        if (SalesRecords == null || SalesRecords.isEmpty()) {
            SalesRecords = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                SalesRecord record = csvReader.readRandomRecord();
                record.setEventTime(DateUtils.getCurrentDateString());
                SalesRecords.add(record);
            }
            uploadList(SalesRecords);
        }


        itemCounter = SalesRecords.size();

    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnBack = findViewById(R.id.m_title_btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewItemToTop();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new SalesAdapter(SalesRecords);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void addNewItemToTop() {
        SalesRecord record = csvReader.readRandomRecord();
        record.setEventTime(DateUtils.getCurrentDateString());
        LoadingAlertDialog loadingAlertDialog = new LoadingAlertDialog(GoInsightActivity.this);
        loadingAlertDialog.show("Uploading");
        uploadOneItem(record, loadingAlertDialog);
    }

    private void uploadOneItem(SalesRecord newItem, LoadingAlertDialog loadingAlertDialog) {
        ArrayList<SalesRecord> objects = new ArrayList<>();
        objects.add(newItem);
        List<Map<String, Object>> list = getUploadList(objects);
        uploadData(newItem, list, loadingAlertDialog);
    }

    private void uploadList(List<SalesRecord> itemList) {
        List<Map<String, Object>> list = getUploadList(itemList);
        uploadData(null, list, null);
    }

    private void uploadData(SalesRecord newItem, List<Map<String, Object>> list, LoadingAlertDialog loadingAlertDialog) {
        new Thread(() -> {
            try {
                SdkObject sdkObject = StoreSdk.getInstance().goInsightApi().syncTerminalBizData(list);
                if (sdkObject.getBusinessCode() == 0) {
                    runOnUiThread(() -> {
                        if (newItem != null) {
                            addOneitem(newItem);
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(GoInsightActivity.this, "Upload failed: " + sdkObject.getBusinessCode() + " > message : " + sdkObject.getMessage(), Toast.LENGTH_LONG).show();

                    });
                }
                if (loadingAlertDialog != null) {
                    loadingAlertDialog.dismiss();
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    if (loadingAlertDialog != null) {
                        loadingAlertDialog.dismiss();
                    }
                    Toast.makeText(GoInsightActivity.this, "Upload failed:" + e, Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void addOneitem(SalesRecord newItem) {
        adapter.addItemToTop(newItem);
        recyclerView.scrollToPosition(0);
        saveToLocal();
        itemCounter++;
    }

    /**
     * save up to 20 records
     */
    private void saveToLocal() {
        List<SalesRecord> saveList = new ArrayList<>();
        if (adapter.getList().size() > 20) {
            for (int i = 0; i < 20; i++) {
                saveList.add(adapter.getList().get(i));
            }
        } else {
            saveList = adapter.getList();
        }
        spUtil.setDataList(getApplicationContext(), SP_SALES, saveList);
    }

    private void setupCSVReader() {
        csvReader = new RandomCSVReader(this, CSV_FILE_NAME);
        Log.d(TAG, "init csv reader success");
    }


    @NonNull
    private static List<Map<String, Object>> getUploadList(List<SalesRecord> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (SalesRecord SalesRecord : itemList) {
            Map<String, Object> map = transferToUploadMap(SalesRecord);
            list.add(map);
        }

        return list;
    }

    @NonNull
    private static Map<String, Object> transferToUploadMap(SalesRecord newItem) {
        Map<String, Object> map = new HashMap<>();


        map.put("segment", newItem.getSegment());
        map.put("city", newItem.getCity());
        map.put("state", newItem.getState());
        map.put("region", newItem.getRegion());
        map.put("category", newItem.getCategory());
        map.put("product", newItem.getProduct());

        map.put("sales", newItem.getSales());
        map.put("quantity", newItem.getQuantity());
        map.put("discount", newItem.getDiscount());
        map.put("profit", newItem.getProfit());

        return map;
    }
}