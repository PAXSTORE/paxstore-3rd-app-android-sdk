package com.pax.android.demoapp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

//to store result cross process, replace shared preferences.
public class SPUtil {


    public void setString(String tag, String value) {
        BaseApplication.appPreferences.put(tag, value);
    }

    public String getString(String tag) {
        String value = BaseApplication.appPreferences.getString(tag,null);
        return value;

    }

    /**
     * save List
     * @param tag
     * @param datalist
     */
    public <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        BaseApplication.appPreferences.put(tag, strJson);

    }

    /**
     * get List
     * @param tag
     * @return
     */
    public <T> List<T> getDataList(String tag) {
        List<T> datalist=new ArrayList<T>();
        String strJson = BaseApplication.appPreferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
        }.getType());
        return datalist;

    }
}