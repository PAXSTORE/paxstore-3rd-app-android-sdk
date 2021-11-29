package com.pax.android.demoapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.pax.market.android.app.sdk.StoreSdk;
import com.pax.market.api.sdk.java.base.dto.DataQueryResultObject;
import com.pax.market.api.sdk.java.base.exception.NotInitException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.pax.android.demoapp.GenerateDataActivity.trans_bar;
import static com.pax.android.demoapp.GenerateDataActivity.trans_line;
import static com.pax.android.demoapp.GenerateDataActivity.trans_pi;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoFragment extends Fragment implements FragmentReceiver {
    private static final String TAG = GoFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ChartData chartData_line, chartData_bar, chartData_pi;
    private Map<String, Boolean> flags = new ConcurrentHashMap<>();


    private OnFragmentInteractionListener mListener;

    private LinearLayout mContentWrap;
    private View contentView, mMore, mNodata;
    private View mProGress;
    private View mNoData;
    private long lastShowHintTime;


    private BarChart mBarChart;
    private LineChart mLineChart;
    private PieChart mPieChart;
    private PopupWindow mPopWindow;

    public GoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GoFragment newInstance(String param1, String param2) {
        GoFragment fragment = new GoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private List<String> getLabels(int count) {
        List<String> chartLabels = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            chartLabels.add("X" + i);
        }
        return chartLabels;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    public void creareBarChart(BarChart barChart, List<String> colums, List<Object[]> rows) {

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);


        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());


        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            try {
                ArrayList<BarEntry> barEntries = new ArrayList<>();
                barEntries.add(new BarEntry(i, Float.parseFloat((String) rows.get(i)[1]), rows.get(i)[0]));
                BarDataSet barDataSet = new BarDataSet(barEntries, (String) rows.get(i)[0]);
                barDataSet.setColor(colors.get(i % colors.size()));
                dataSets.add(barDataSet);
            } catch (NumberFormatException e) {
                showParseErrorHint();
            }
        }


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "";
//                return (String)(barEntries.get((int) value).getData());
//                return super.getFormattedValue(value);
            }
        });


        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);


        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        l.setWordWrapEnabled(true);


        BarData theData = new BarData(dataSets);
        theData.setBarWidth(0.9f);
        barChart.setData(theData);

    }

    public void createLineChart(LineChart lineChart, List<String> colums, final List<Object[]> rows) {
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawBorders(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // 拖动和缩放
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);

        final ArrayList<Entry> values = new ArrayList<>();


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float position) {

                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i).getX() == position) {
                        return (String) ((rows.get((int) position))[0]);
                    }
                }

                return "";
            }
        });

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);


        Legend lfc = lineChart.getLegend();
        lfc.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        lfc.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        lfc.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        lfc.setDrawInside(false);
        lfc.setForm(Legend.LegendForm.SQUARE);
        lfc.setFormSize(9f);
        lfc.setTextSize(11f);
        lfc.setXEntrySpace(4f);
        lfc.setWordWrapEnabled(true);


        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();


        for (int i = 0; i < rows.size(); i++) {
            try {
                values.add(new Entry(i, Float.parseFloat((String) rows.get(i)[1])));
            } catch (NumberFormatException e) {
                showParseErrorHint();
            }

        }
        LineDataSet d = new LineDataSet(values, "");

        // draw dashed line
        d.enableDashedLine(10f, 5f, 0f);

        // black lines and points
        d.setColor(colors.get(0));
        d.setCircleColor(colors.get(0));

        // line thickness and point size
        d.setLineWidth(1f);
        d.setCircleRadius(3f);

        // draw points as solid circles
        d.setDrawCircleHole(false);

        // customize legend entry
        d.setFormLineWidth(1f);
        d.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        d.setFormSize(15.f);

        // text size of values
        d.setValueTextSize(9f);

        // draw selection line as dashed
        d.enableDashedHighlightLine(10f, 5f, 0f);

        // set the filled area
        d.setDrawFilled(true);

        dataSets.add(d);


        LineData data = new LineData(dataSets);
        lineChart.setData(data);
    }

    private void showParseErrorHint() {
        if (System.currentTimeMillis() - lastShowHintTime > 5_000L) {// toast filter.
            lastShowHintTime = System.currentTimeMillis();
            Toast.makeText(getContext(), "Please select none format for your data", Toast.LENGTH_LONG).show();
        }
    }

    public void createPieChart(PieChart pieChart, List<String> colums, List<Object[]> rows) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);


        pieChart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend lfc = pieChart.getLegend();
        lfc.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        lfc.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        lfc.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        lfc.setDrawInside(false);
        lfc.setForm(Legend.LegendForm.SQUARE);
        lfc.setFormSize(9f);
        lfc.setTextSize(11f);
        lfc.setXEntrySpace(4f);
        lfc.setWordWrapEnabled(true);
        // entry label styling
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            try {
                entries.add(new PieEntry(Float.parseFloat((String) rows.get(i)[1]), (String) rows.get(i)[0]));
            } catch (NumberFormatException e) {
                showParseErrorHint();
            }
        }


        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colorsvg = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colorsvg.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colorsvg.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colorsvg.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colorsvg.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colorsvg.add(c);

        colorsvg.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colorsvg);

        PieData datafr = new PieData(dataSet);
        datafr.setValueFormatter(new PercentFormatter(pieChart));
        datafr.setValueTextSize(11f);
        datafr.setValueTextColor(Color.WHITE);
        pieChart.setData(datafr);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.go, container, false);
        mContentWrap = view.findViewById(R.id.contentWrap);
        mMore = view.findViewById(R.id.more);
        mProGress = view.findViewById(R.id.pro_wrap_fragment);
        mNoData = view.findViewById(R.id.nodata_view);
        mProGress.setVisibility(View.VISIBLE);
        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show pop window
                showPopupWindow(getContext(), mContentWrap);
            }
        });
        mNoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryWrap();
                mNoData.setVisibility(View.GONE);
                mProGress.setVisibility(View.VISIBLE);
            }
        });
        return view;


    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPopWindow != null) {
            mPopWindow.dismiss();
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRecive(Context context, Intent intent) {

    }

    public void update_chart() {
        if (getActivity() == null) {
            Log.w(TAG, "getActivity() == null");
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mProGress.setVisibility(View.GONE);
                View view = getView();

                //判断chartData
                ChartData chartData_line = getChartData_line();
                ChartData chartData_bar = getChartData_bar();
                ChartData chartData_pi = getChartData_pi();
                if (chartData_line != null && !chartData_line.isEmpty()
                        && chartData_bar != null && !chartData_bar.isEmpty()
                        && chartData_pi != null && !chartData_pi.isEmpty()) {
                    view.findViewById(R.id.chart_bar_wrap).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.chart_line_wrap).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.chart_pie_wrap).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.nodata_view).setVisibility(View.GONE);

                    //******
                    List<String> colums_line = chartData_line.getColumus();
                    List<Object[]> datas_line = chartData_line.getDatas();

                    List<String> colums_bar = chartData_bar.getColumus();
                    List<Object[]> datas_bar = chartData_bar.getDatas();

                    List<String> colums_pi = chartData_pi.getColumus();
                    List<Object[]> datas_pi = chartData_pi.getDatas();
                    //******


                    //load chart
                    mBarChart = view.findViewById(R.id.chart_bar);
                    ((TextView) view.findViewById(R.id.bar_title)).setText(chartData_bar.getTitle());
                    view.findViewById(R.id.chart_bar_wrap).setVisibility(View.VISIBLE);
                    creareBarChart(mBarChart, colums_bar, datas_bar);

                    //load linechart
                    mLineChart = view.findViewById(R.id.chart_line);
                    ((TextView) view.findViewById(R.id.line_title)).setText(chartData_line.getTitle());
                    view.findViewById(R.id.chart_line_wrap).setVisibility(View.VISIBLE);
                    createLineChart(mLineChart, colums_line, datas_line);


//            if(colums.size()<=2){
//                //load piechart
//                mPieChart = view.findViewById(R.id.chart_pie);
//                ((TextView)view.findViewById(R.id.pie_title)).setText(chartData_pi.getTitle());
//                view.findViewById(R.id.chart_pie_wrap).setVisibility(View.VISIBLE);
//                createPieChart(mPieChart,colums_pi,datas_pi);
//            }

                    //load piechart
                    mPieChart = view.findViewById(R.id.chart_pie);
                    ((TextView) view.findViewById(R.id.pie_title)).setText(chartData_pi.getTitle());
                    view.findViewById(R.id.chart_pie_wrap).setVisibility(View.VISIBLE);
                    createPieChart(mPieChart, colums_pi, datas_pi);


                } else {
                    view.findViewById(R.id.chart_bar_wrap).setVisibility(View.GONE);
                    view.findViewById(R.id.chart_line_wrap).setVisibility(View.GONE);
                    view.findViewById(R.id.chart_pie_wrap).setVisibility(View.GONE);
                    view.findViewById(R.id.nodata_view).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void notifyFragment(Context context, Object object) {
        Log.d(TAG, "getDAta");
        update_chart();
    }

    @Override
    public void loadDataError(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopupWindow(Context context, View rootView) {
        //设置contentView
        View contentView = LayoutInflater.from(context).inflate(R.layout.more_pop, null);
        mPopWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);

        mPopWindow.setFocusable(true);

        mPopWindow.setOutsideTouchable(true);

        mPopWindow.update();

        mPopWindow.setBackgroundDrawable(new ColorDrawable(0x55000000));

        Button mGenarate = contentView.findViewById(R.id.pop_generate);
        mGenarate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GenerateDataActivity.class);
                startActivityForResult(intent, 1000);
            }
        });

        mPopWindow.showAtLocation(rootView, Gravity.TOP | Gravity.RIGHT, 0, dip2px(context, 46) + getStatusBarHeight(context));

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //TODO now it's visible to user
            Log.d(TAG, "yes");
            queryWrap();
        } else {
            //TODO now it's invisible to user
            Log.d(TAG, "no");
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////// get data ///////////////////////////////
    private void queryWrap() {
        queryBizData("3hi0fs8i", ChartType.LINE);
        queryBizData("v664nkfc", ChartType.BAR);
        queryBizData("7a5ck60a", ChartType.PI);
    }

    private void queryBizData(final String queryCode, final ChartType type) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataQueryResultObject terminalData = null;
                try {
                    terminalData = StoreSdk.getInstance().goInsightApi().findMerchantData(queryCode);
                    Log.d(TAG, "msg::" + terminalData.getMessage());
                    List<DataQueryResultObject.Column> columns = terminalData.getColumns();
                    //transform to chartData
                    ChartData chart;
                    switch (type) {
                        case BAR:
                            chart = trans_bar(terminalData);
                            chartData_bar = chart;
                            flags.put("BAR", true);
                            break;
                        case LINE:
                            chart = trans_line(terminalData);
                            chartData_line = chart;
                            flags.put("LINE", true);
                            break;
                        case PI:
                            chart = trans_pi(terminalData);
                            chartData_pi = chart;
                            flags.put("PI", true);
                            break;
                    }

                } catch (NotInitException e) {
                    loadDataError("Init failed: " + e.getMessage());
                    flags.put("LINE", false);
                    flags.put("BAR", false);
                    flags.put("PI", false);
                }


                if (flags.size() == 3) {
                    if (terminalData != null && terminalData.getBusinessCode() != 0) {
                        loadDataError("BusinessCode: " + terminalData.getBusinessCode() + " Message: " + terminalData.getMessage());
                    }
                    notifyFragment(getActivity(), null);
                }
            }
        });
        thread.start();

    }

    public ChartData getChartData_line() {
        return chartData_line;
    }

    public ChartData getChartData_bar() {
        return chartData_bar;
    }

    public ChartData getChartData_pi() {
        return chartData_pi;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
