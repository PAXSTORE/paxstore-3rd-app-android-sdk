package com.pax.android.demoapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private BarChart mBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.BLACK);
        colors.add(Color.GRAY);
        colors.add(Color.RED);
        colors.add(Color.GREEN);

        mBarChart = (BarChart) findViewById(R.id.chart);
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, 1));
        barEntries.add(new BarEntry(1, 2));
        barEntries.add(new BarEntry(2, 3));
        BarDataSet iBarDataSet = new BarDataSet(barEntries, "bar label");
        iBarDataSet.setColors(colors);
        iBarDataSet.setValueTextColors(colors);
        BarData barData = new BarData(iBarDataSet); // 可以添加多个set，即可化成group组
        mBarChart.setData(barData);

//        mBarChart.groupBars(1980f, 20, 0);  // 设置group组间隔
        mBarChart.setFitBars(true);    // 在bar开头结尾两边添加一般bar宽的留白
        mBarChart.setDrawValueAboveBar(false);    // 所有值都绘制在柱形外顶部，而不是柱形内顶部。默认true
        mBarChart.setDrawBarShadow(false);   // 柱形阴影，一般有值被绘制，但是值到顶部的位置为空，这个方法设置也画这部分，但是性能下降约40%，默认false



        Legend legend = mBarChart.getLegend(); // 获取图例，但是在数据设置给chart之前是不可获取的
        legend.getCalculatedLineSizes();
        legend.setEnabled(true);    // 是否绘制图例
        legend.setTextColor(Color.GRAY);    // 图例标签字体颜色，默认BLACK
        legend.setTextSize(12); // 图例标签字体大小[6,24]dp,默认10dp
        legend.setTypeface(null);   // 图例标签字体
        legend.setWordWrapEnabled(false);    // 当图例超出时是否换行适配，这个配置会降低性能，且只有图例在底部时才可以适配。默认false
        legend.setMaxSizePercent(1f); // 设置，默认0.95f,图例最大尺寸区域占图表区域之外的比例
        legend.setForm(Legend.LegendForm.SQUARE);   // 设置图例的形状，SQUARE, CIRCLE 或者 LINE
        legend.setFormSize(8); // 图例图形尺寸，dp，默认8dp
        legend.setXEntrySpace(6);  // 设置水平图例间间距，默认6dp
        legend.setYEntrySpace(0);  // 设置垂直图例间间距，默认0
        legend.setFormToTextSpace(5);    // 设置图例的标签与图形之间的距离，默认5dp
        legend.setWordWrapEnabled(true);   // 图标单词是否适配。只有在底部才会有效，
        legend.setCustom(new LegendEntry[]{new LegendEntry("label1", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED),
                new LegendEntry("label2", Legend.LegendForm.CIRCLE, 10, 5, null, Color.GRAY),
                new LegendEntry("label3", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)}); // 这个应该是之前的setCustom(int[] colors, String[] labels)方法
        // 这个方法会把前面设置的图例都去掉，重置为指定的图例。
//        legend.resetCustom();   // 去掉上面方法设置的图例，然后之前dataSet中设置的会重新显示。
//        legend.setExtra(new int[]{Color.RED, Color.GRAY, Color.GREEN}, new String[]{"label1", "label2", "label3"}); // 添加图例，颜色与label数量要一致。
        // 如果前面已经在dataSet中设置了颜色，那么之前的图例就存在，这个只是添加在后面的图例，并不一定有对应数据。

    }
}
