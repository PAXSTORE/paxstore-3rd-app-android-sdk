package com.pax.android.demoapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pax.market.android.app.sdk.BaseApiService;
import com.pax.market.android.app.sdk.LocationService;
import com.pax.market.android.app.sdk.StoreSdk;
import com.pax.market.android.app.sdk.dto.LocationInfo;
import com.pax.market.android.app.sdk.dto.OnlineStatusInfo;
import com.pax.market.android.app.sdk.dto.TerminalInfo;
import com.pax.market.api.sdk.java.base.constant.ResultCode;

import com.pax.market.api.sdk.java.base.dto.MsgTagObject;
import com.pax.market.api.sdk.java.base.dto.SdkObject;
import com.pax.market.api.sdk.java.base.dto.UpdateObject;
import com.pax.market.api.sdk.java.base.exception.NotInitException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link APIFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link APIFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class APIFragment extends Fragment {
    private static final String TAG = APIFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private TextView bannerTitleTV;
    private TextView bannerTextTV;
    private TextView bannerSubTextTV;
    private TextView versionTV;
    private LinearLayout openClientlayout;
    private Switch tradingStateSwitch;
    private Button getTerminalInfoBtn;
    private Button btnGetAllTag;
    private CloudMsgTagAdapter cloudMsgTagAdapter;
    private List<String> tags = new ArrayList<>();
    private ListView tagListView;

    private ScrollView scrollView;
    private LinearLayout lvRetrieveData,checkUpdate,openDownloadList,lvActivate, lvActivateHide, lvAttachTag, lvDeleteTag;
    private EditText etTid;
    private ImageView mImgRetrieve, mImgActivate, mImgMsgTab;
    private LinearLayout lvChildRetrieve;
    private Button getTerminalLocation, getOnlineStatus, activateTerminal; // todo remove
    private OnFragmentInteractionListener mListener;
    private boolean isDataExpanded, isActivateExpanded, isMsgTabExpanded;
    public APIFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment APIFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static APIFragment newInstance(String param1, String param2) {
        APIFragment fragment = new APIFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.api, container, false);

        bannerTitleTV = (TextView) view.findViewById(R.id.banner_title);
        bannerTextTV = (TextView) view.findViewById(R.id.banner_text);
        bannerSubTextTV = (TextView) view.findViewById(R.id.banner_sub_text);
        tradingStateSwitch = (Switch) view.findViewById(R.id.tradingStateSwitch);
        openClientlayout = (LinearLayout) view.findViewById(R.id.openAppDetail);
        versionTV = (TextView) view.findViewById(R.id.versionText);
        versionTV.setText(getResources().getString(R.string.label_version_text) + " " + BuildConfig.VERSION_NAME);
        openDownloadList = (LinearLayout) view.findViewById(R.id.open_downloadlist_page);

        lvActivate = view.findViewById(R.id.lv_activate);
        lvActivateHide = view.findViewById(R.id.lv_activate_hide);
        lvAttachTag = view.findViewById(R.id.lv_attach_tag);
        lvDeleteTag = view.findViewById(R.id.lv_delete_tag);
        btnGetAllTag = view.findViewById(R.id.get_tag_btn);
        btnGetAllTag = view.findViewById(R.id.get_tag_btn);

        etTid = view.findViewById(R.id.et_tid);
        activateTerminal = view.findViewById(R.id.btn_activate);
        mImgActivate = view.findViewById(R.id.img_activate);

        tagListView = view.findViewById(R.id.tag_list);
        cloudMsgTagAdapter = new CloudMsgTagAdapter(getContext(), tags, R.layout.param_detail_list_item);
        tagListView.setAdapter(cloudMsgTagAdapter);

        checkUpdate = (LinearLayout) view.findViewById(R.id.check_update);
        lvRetrieveData = (LinearLayout) view.findViewById(R.id.lv_retrieve_data);
        lvChildRetrieve = (LinearLayout) view.findViewById(R.id.lv_childs_retrieve);
        mImgRetrieve = (ImageView) view.findViewById(R.id.img_retrieve_data);
        getTerminalLocation = (Button) view.findViewById(R.id.get_location);
        //switch to set trading status.
        tradingStateSwitch.setChecked(((BaseApplication) getContext().getApplicationContext()).isReadyToUpdate());
        tradingStateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ((BaseApplication) getContext().getApplicationContext()).setReadyToUpdate(true);
                } else {
                    ((BaseApplication) getContext().getApplicationContext()).setReadyToUpdate(false);
                }
            }
        });

        //open paxtore client
        openClientlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //put app 'NeptuneService' package name here for demo.
                //if the market don't have this app, it will show app not found, else will go to detail page in PAXSTORE market
                openAppDetail(getActivity().getPackageName());
            }
        });



        openDownloadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreSdk.getInstance().openDownloadListPage(getActivity().getPackageName(), getActivity().getApplicationContext());
            }
        });

        checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if update available from PAXSTORE.
                showProgress();
                Thread thread =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final UpdateObject updateObject = StoreSdk.getInstance().updateApi().checkUpdate(BuildConfig.VERSION_CODE, getContext().getPackageName());
                            LauncherActivity.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    dismissLoadingDialog();
                                    if (updateObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                                        if (updateObject.isUpdateAvailable()) {
                                            Toast.makeText(getContext(), "Update is available", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getContext(), "No Update available", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "errmsg:>>" + updateObject.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.w("MessagerActivity", "updateObject.getBusinessCode():"
                                                + updateObject.getBusinessCode() + "\n msg:" + updateObject.getMessage());
                                    }
                                }
                            });

                        } catch (NotInitException e) {
                            Log.e(TAG, "e:" + e);
                        }
                    }
                }) ;

                thread.start();

            }
        });

        lvActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isActivateExpanded) {
                    isActivateExpanded = false;
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                            hideSoftInputFromWindow(view.getWindowToken(), 0);
                    mImgActivate.setImageResource(R.mipmap.list_btn_arrow);
                    lvActivateHide.setVisibility(View.GONE);
                } else {
                    etTid.requestFocus();
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).
                            toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
                    isActivateExpanded = true;
                    mImgActivate.setImageResource(R.mipmap.list_btn_arrow_down);
                    lvActivateHide.setVisibility(View.VISIBLE);
                }
            }
        });

        lvAttachTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CloudMsgDialog.newBuilder().context(getActivity()).title(getString(R.string.attach_msg_tags)).listener(new CloudMsgDialog.ClickListener() {
                    @Override
                    public void onClick(final String tags) {
                        if (!tags.isEmpty()) {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final SdkObject sdkObject = StoreSdk.getInstance().syncMsgTabApi().attachMsgTag(Arrays.asList(tags.split(",")));
                                        Log.d(TAG, "sdkObject:" + sdkObject.toString());
                                            LauncherActivity.getHandler().post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String msg;
                                                    if (sdkObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                                                        msg = "Operation succeed!";
                                                    } else {
                                                        msg = "Operation failed: " + sdkObject.toString();
                                                    }
                                                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    } catch (NotInitException e) {
                                        Log.e(TAG, "e:" + e);
                                    }
                                }
                            });
                            thread.start();
                        }
                    }
                }).build().show();
            }
        });

        lvDeleteTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CloudMsgDialog.newBuilder().context(getActivity()).title(getString(R.string.delete_msg_tags)).listener(new CloudMsgDialog.ClickListener() {
                    @Override
                    public void onClick(final String tags) {
                        if (!tags.isEmpty()) {
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final SdkObject sdkObject = StoreSdk.getInstance().syncMsgTabApi().detachMsgTag(Arrays.asList(tags.split(",")));
                                        LauncherActivity.getHandler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                String msg;
                                                if (sdkObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                                                    msg = "Operation succeed!";
                                                } else {
                                                    msg = "Operation failed: " + sdkObject.toString();
                                                }
                                                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (NotInitException e) {
                                        Log.e(TAG, "e:" + e);
                                    }
                                }
                            });
                            thread.start();
                        }
                    }
                }).build().show();
            }
        });


        btnGetAllTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final MsgTagObject msgTagObject = StoreSdk.getInstance().syncMsgTabApi().getAllTag();
                            LauncherActivity.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (msgTagObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                                        if(msgTagObject.getTags()!= null && !msgTagObject.getTags().isEmpty()) {
                                            cloudMsgTagAdapter.loadData(msgTagObject.getTags());
                                        } else {
                                            Toast.makeText(getContext(), "Tags is empty", Toast.LENGTH_SHORT).show();
                                            cloudMsgTagAdapter.loadData(new ArrayList<String>());
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "Get tags failed:" + msgTagObject.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (NotInitException e) {
                            Log.e(TAG, "e:" + e);
                        }
                    }
                }) ;
                thread.start();
            }
        });

        activateTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etTid.getText() == null || etTid.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(), "Please input TID", Toast.LENGTH_LONG).show();
                    return;
                }
                showProgress();
                Thread thread =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final SdkObject sdkObject = StoreSdk.getInstance().activateApi().initByTID(etTid.getText().toString());
                            Log.d(TAG, "sdkObject:" + sdkObject.toString());
                            LauncherActivity.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    String msg = "";
                                    if (sdkObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                                        msg = "Activation succeed!";
                                    } else {
                                        msg = "Activation failed: " + sdkObject.toString();
                                    }
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                    dismissLoadingDialog();
                                }
                            });
                        } catch (NotInitException e) {
                            Log.e(TAG, "e:" + e);
                        }
                    }
                }) ;

                thread.start();
            }
        });

        lvRetrieveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataExpanded) {
                    isDataExpanded = false;
                    mImgRetrieve.setImageResource(R.mipmap.list_btn_arrow);
                    lvChildRetrieve.setVisibility(View.GONE);
                } else {
                    isDataExpanded = true;
                    mImgRetrieve.setImageResource(R.mipmap.list_btn_arrow_down);
                    lvChildRetrieve.setVisibility(View.VISIBLE);
                }
            }
        });

        getTerminalLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreSdk.getInstance().startLocate(getActivity().getApplicationContext(), new LocationService.LocationCallback() {
                    @Override
                    public void locationResponse(LocationInfo locationInfo) {
                        Log.d(TAG, "Get Location Result：" + locationInfo.toString());
                        Toast.makeText(getContext(),
                                "Get Location Result：" + locationInfo.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        getOnlineStatus = (Button) view.findViewById(R.id.get_online_status);
        getOnlineStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnlineStatusInfo onlineStatusFromPAXSTORE = StoreSdk.getInstance().getOnlineStatusFromPAXSTORE(getActivity().getApplicationContext());
                Toast.makeText(getContext(), onlineStatusFromPAXSTORE.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        getTerminalInfoBtn = view.findViewById(R.id.GetTerminalInfo);
        getTerminalInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreSdk.getInstance().getBaseTerminalInfo(getContext().getApplicationContext(), new BaseApiService.ICallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        TerminalInfo terminalInfo = (TerminalInfo) obj;
                        Log.i("onSuccess: ", terminalInfo.toString());
                        Toast.makeText(getContext().getApplicationContext(), terminalInfo.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.i("onError: ", e.toString());
                        Toast.makeText(getContext().getApplicationContext(), "getTerminalInfo Error:" + e.toString(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });

        scrollView = view.findViewById(R.id.root);
        scrollView.smoothScrollTo(0, 0);

        return view;
    }

    LoadingAlertDialog dialog;
    private void showProgress() {
        if (dialog != null && dialog.isShowing()) {
            return;
        }
        dialog = new LoadingAlertDialog(getContext());
        dialog.show(getString(R.string.label_loading));
    }

    private void dismissLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
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






    private void openAppDetail(String packageName) {
        String url = String.format("market://detail?id=%s", packageName);
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setClassName("com.pax.market.android.app", "com.pax.market.android.app.presentation.search.view.activity.SearchAppDetailActivity");
        intent.putExtra("app_packagename", packageName);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
