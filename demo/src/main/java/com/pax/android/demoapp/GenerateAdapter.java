package com.pax.android.demoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GenerateAdapter extends BaseAdapter {
    private List<DataBean> mList;
    private Context mContext;

    public GenerateAdapter(List<DataBean> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    public List<DataBean> getmList() {
        return mList;
    }

    public void setmList(List<DataBean> mList) {
        this.mList = mList;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.data_item, null);
        }

        if(position%2==0){
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.gray_pax));
        }

        TextView Merchant = convertView.findViewById(R.id.merchant);
        TextView Reseller = convertView.findViewById(R.id.reseller);
        TextView Acquirer_Type = convertView.findViewById(R.id.acquirer);
        TextView Amount = convertView.findViewById(R.id.amount);
        TextView Purchase_Time = convertView.findViewById(R.id.purchase_time);
        TextView Op_Number = convertView.findViewById(R.id.op_number);
        TextView Purchase_ID = convertView.findViewById(R.id.purchase_id);

        Merchant.setText(mList.get(position).getMerchant());
        Reseller.setText(mList.get(position).getReseller());
        Acquirer_Type.setText(mList.get(position).getAcquirer_Type());
        Amount.setText(mList.get(position).getAmount());
        Purchase_Time.setText(mList.get(position).getPurchase_Time());
        Op_Number.setText(mList.get(position).getOp_Number());
        Purchase_ID.setText(mList.get(position).getPurchase_ID());

        return convertView;
    }
}
