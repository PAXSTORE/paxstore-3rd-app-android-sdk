package com.pax.android.demoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pax.android.demoapp.R;

import java.util.List;


public class CloudMsgTagAdapter extends BaseAdapter {

    private List<String> data;
    private LayoutInflater layoutInflater;
    private Context context;
    private int resId;

    public CloudMsgTagAdapter(Context context, List<String> data, int resId) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(resId, null);
            holder.label = convertView.findViewById(R.id.label);
            holder.value = convertView.findViewById(R.id.value);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.value.setText(data.get(position));
        holder.label.setText(String.valueOf(position + 1));
        return convertView;
    }

    public void loadData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}