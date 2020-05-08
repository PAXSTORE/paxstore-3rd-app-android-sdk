package com.pax.android.demoapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoFragment extends Fragment {
    private static final String TAG = GoFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LinearLayout mContentWrap;
    private View contentView;
    private View mMore;


    private BarChart mBarChart;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        View view = getView();

        //判断chartData
        if ( ((LauncherActivity)getActivity()).getChartData() != null && !((LauncherActivity)getActivity()).getChartData().isEmpty()) {
            //load chart

            List<Integer> colors = new ArrayList<>();
            colors.add(Color.BLACK);
            colors.add(Color.GRAY);
            colors.add(Color.RED);
            colors.add(Color.GREEN);
            mBarChart = (BarChart) view.findViewById(R.id.chart);
            mBarChart.setVisibility(View.VISIBLE);
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
            legend.setForm(Legend.LegendForm.SQUARE);   // 设置图例的形状，SQUARE, CIRCLE 或者 LINE
            legend.setWordWrapEnabled(true);   // 图标单词是否适配。只有在底部才会有效，
            legend.setCustom(new LegendEntry[]{new LegendEntry("label1", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED),
                    new LegendEntry("label2", Legend.LegendForm.CIRCLE, 10, 5, null, Color.GRAY),
                    new LegendEntry("label3", Legend.LegendForm.CIRCLE, 10, 5, null, Color.RED)}); // 这个应该是之前的setCustom(int[] colors, String[] labels)方法

        }
         else {
            mBarChart = (BarChart) view.findViewById(R.id.chart);
            mBarChart.setVisibility(View.GONE);

            contentView = LayoutInflater.from(getContext()).inflate(R.layout.no_data, null);
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mContentWrap.addView(contentView, ll);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.go, container, false);
        mContentWrap = view.findViewById(R.id.contentWrap);
        mMore = view.findViewById(R.id.more);

        mMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show pop window
                showPopupWindow(getContext(), mContentWrap);
            }
        });
        return view;


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


    private void showPopupWindow(Context context, View rootView) {
        //设置contentView
        View contentView = LayoutInflater.from(context).inflate(R.layout.more_pop, null);
        PopupWindow mPopWindow = new PopupWindow(contentView,
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
                startActivityForResult(intent,1000);
            }
        });

        mPopWindow.showAtLocation(rootView, Gravity.TOP | Gravity.RIGHT, 0, dip2px(context, 46) + getStatusBarHeight(context));

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

}
