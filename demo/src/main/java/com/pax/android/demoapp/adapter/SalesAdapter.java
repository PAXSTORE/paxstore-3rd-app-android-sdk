package com.pax.android.demoapp.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pax.android.demoapp.R;
import com.pax.android.demoapp.dto.SalesRecord;

import java.text.DecimalFormat;
import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder> {

    private List<SalesRecord> SalesRecords;
    private DecimalFormat decimalFormat;

    public SalesAdapter(List<SalesRecord> SalesRecords) {
        this.SalesRecords = SalesRecords;
        this.decimalFormat = new DecimalFormat("#.##");
    }

    public List<SalesRecord> getList() {
        return SalesRecords;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.data_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SalesRecord item = SalesRecords.get(position);

        // 设置商品名称和日期
        holder.tvProductName.setText(item.getProduct());
        holder.tvDate.setText(item.getEventTime());

        // 设置销售额（格式化为美元）
        String salesText = decimalFormat.format(item.getSales()) + "$";
        holder.tvSalesAmount.setText(salesText);

        // 设置利润（根据正负设置颜色）

        holder.tvProfitAmount.setText(decimalFormat.format(item.getProfit()) + "$");

        // 设置类别
        holder.tvCategory.setText(item.getCategory());
    }

    @Override
    public int getItemCount() {
        return SalesRecords.size();
    }

    // 添加新项目到顶部的方法
    public void addItemToTop(SalesRecord newItem) {
        SalesRecords.add(0, newItem);
        notifyItemInserted(0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvProductName;
        public TextView tvDate;
        public TextView tvSalesAmount;
        public TextView tvProfitAmount;
        public TextView tvCategory;

        public ViewHolder(View view) {
            super(view);
            tvProductName = view.findViewById(R.id.tv_name);
            tvDate = view.findViewById(R.id.tv_date);
            tvSalesAmount = view.findViewById(R.id.tv_total_sale);
            tvProfitAmount = view.findViewById(R.id.tv_profit);
            tvCategory = view.findViewById(R.id.tv_category);
        }
    }
}