package com.pax.android.demoapp;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pax.market.android.app.sdk.util.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

//to store result cross process, replace shared preferences.
public class SPUtil {


    public void setString(Context context, String tag, String value) {
       PreferencesUtils.putString(context, tag, value);
    }

    public String getString(Context context, String tag) {
        String value = PreferencesUtils.getString(context, tag,null);
        return value;

    }

    /**
     * save List
     * @param tag
     * @param datalist
     */
    public <T> void setDataList(Context context, String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        PreferencesUtils.putString(context, tag, strJson);

    }

    /**
     * get List
     * @param tag
     * @return
     */
    public <T> List<T> getDataList(Context context, String tag) {
        List<T> datalist=new ArrayList<T>();
        String strJson = PreferencesUtils.getString(context, tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
        }.getType());
        return datalist;

    }
}