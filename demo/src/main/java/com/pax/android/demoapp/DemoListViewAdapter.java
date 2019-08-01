package com.pax.android.demoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


public class DemoListViewAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    private int resId;

    public DemoListViewAdapter(Context context, List<Map<String, Object>> data, int resId){
        this.context = context;
        this.data = data;
        this.layoutInflater= LayoutInflater.from(context);
        this.resId = resId;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView=layoutInflater.inflate(resId, null);
            holder.label = (TextView) convertView.findViewById(R.id.label);
            holder.value = (TextView) convertView.findViewById(R.id.value);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        //bind data
        Map<String, Object> map = data.get(position);

        holder.label.setText((String)map.get("label"));
        holder.value.setText(String.valueOf(map.get("value")));

        return convertView;
    }

    public void loadData(List<Map<String, Object>> data) {

        this.data=data;
        // MANDATORY: Notify that the data has changed
        notifyDataSetChanged();
    }

}