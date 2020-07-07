package com.pax.android.demoapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pax.market.android.app.sdk.AdvertisementDialog;

import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PushFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PushFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PushFragment extends Fragment implements F_Revicer {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private TextView bannerTitleTV;
    private TextView bannerTextTV;
    private TextView bannerSubTextTV;
    private ListViewForScrollView detailListView;
    private LinearLayout nodataLayout;
    private List<Map<String, Object>> datalist;
    private DemoListViewAdapter demoListViewAdapter;


    private SPUtil spUtil;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PushFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PushFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PushFragment newInstance(String param1, String param2) {
        PushFragment fragment = new PushFragment();
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

        spUtil = new SPUtil();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.push, container, false);
        bannerTitleTV = (TextView) view.findViewById(R.id.banner_title);
        bannerTextTV = (TextView) view.findViewById(R.id.banner_text);
        bannerSubTextTV = (TextView) view.findViewById(R.id.banner_sub_text);
        detailListView = view.findViewById(R.id.parameter_detail_list);
        nodataLayout = view.findViewById(R.id.nodata);


        String pushResultBannerTitle = spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TITLE);
        if(DemoConstants.DOWNLOAD_SUCCESS.equals(pushResultBannerTitle)){
            bannerTitleTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TITLE));
            bannerTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TEXT));
            bannerSubTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_SUBTEXT));

            datalist = spUtil.getDataList(DemoConstants.PUSH_RESULT_DETAIL);
            //if have push history, display it. the demo will only store the latest push record.
            if(datalist!=null && datalist.size() >0) {
                //display push history detail
                detailListView.setVisibility(View.VISIBLE);
                nodataLayout.setVisibility(View.GONE);
                demoListViewAdapter = new DemoListViewAdapter(getContext(), datalist, R.layout.param_detail_list_item);
                detailListView.setAdapter(demoListViewAdapter);
            }else{
                //no data. check log for is a correct xml downloaded.
                detailListView.setVisibility(View.GONE);
                nodataLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "File parse error.Please check the downloaded file.", Toast.LENGTH_SHORT).show();

            }
        }else {
            if(DemoConstants.DOWNLOAD_FAILED.equals(pushResultBannerTitle)) {
                bannerTitleTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TITLE));
                bannerTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TEXT));
            }
            //display as no data
            detailListView.setVisibility(View.GONE);
            nodataLayout.setVisibility(View.VISIBLE);
        }


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

    @Override
    public void onRecive(Context context, Intent intent) {
        //update main page UI for push status
        int resultCode = intent.getIntExtra(DemoConstants.DOWNLOAD_RESULT_CODE, 0);
        switch (resultCode) {
            case DemoConstants.DOWNLOAD_STATUS_SUCCESS:
                bannerTitleTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TITLE));
                bannerTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TEXT));
                bannerSubTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_SUBTEXT));
                datalist = spUtil.getDataList(DemoConstants.PUSH_RESULT_DETAIL);
                if (datalist != null && datalist.size() > 0) {
                    //display push history detail
                    detailListView.setVisibility(View.VISIBLE);
                    nodataLayout.setVisibility(View.GONE);
                    demoListViewAdapter = new DemoListViewAdapter(getContext(), datalist, R.layout.param_detail_list_item);
                    detailListView.setAdapter(demoListViewAdapter);
                } else {
                    detailListView.setVisibility(View.GONE);
                    nodataLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "File parse error.Please check the downloaded file.", Toast.LENGTH_SHORT).show();
                }
                break;
            case DemoConstants.DOWNLOAD_STATUS_START:
                bannerTitleTV.setText(DemoConstants.DOWNLOAD_START);
                bannerTextTV.setText("Your push parameters are downloading");
                break;
            case DemoConstants.DOWNLOAD_STATUS_FAILED:
                bannerTitleTV.setText(DemoConstants.DOWNLOAD_FAILED);
                bannerTextTV.setText(spUtil.getString(DemoConstants.PUSH_RESULT_BANNER_TEXT));
                //display as no data
                detailListView.setVisibility(View.GONE);
                nodataLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void notify_fragment(Context context, Object object) {

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



    //

}
