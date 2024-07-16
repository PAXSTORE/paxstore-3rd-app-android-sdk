package com.pax.android.demoapp;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.pax.market.android.app.sdk.BaseApiService;
import com.pax.market.android.app.sdk.StoreSdk;
import com.pax.market.android.app.sdk.dto.OnlineStatusInfo;
import com.pax.market.android.app.sdk.dto.TerminalInfo;
import com.pax.market.api.sdk.java.base.constant.ResultCode;
import com.pax.market.api.sdk.java.base.dto.DownloadResultObject;
import com.pax.market.api.sdk.java.base.dto.LocationObject;
import com.pax.market.api.sdk.java.base.dto.MerchantObject;
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
    List<Long> idList = new ArrayList<>();
    private TextView versionTV;
    private LinearLayout openClientlayout;
    private Switch tradingStateSwitch;
    private Button getTerminalInfoBtn;
    private Button btnGetAllTag;
    private CloudMsgTagAdapter cloudMsgTagAdapter;
    private List<String> tags = new ArrayList<>();
    private ListView tagListView;

    private ScrollView scrollView;
    private LinearLayout lvRetrieveData,checkUpdate,openDownloadList, lvAttachTag, lvDeleteTag , lvGetLastSuccess;
    private ImageView mImgRetrieve;
    private LinearLayout lvChildRetrieve;
    private Button getTerminalLocation, getOnlineStatus, getMerchantInfo; // todo remove
    private OnFragmentInteractionListener mListener;
    private boolean isDataExpanded;
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.api, container, false);

        tradingStateSwitch = (Switch) view.findViewById(R.id.tradingStateSwitch);
        openClientlayout = (LinearLayout) view.findViewById(R.id.openAppDetail);
        versionTV = (TextView) view.findViewById(R.id.versionText);
        versionTV.setText(getResources().getString(R.string.label_version_text) + " " + BuildConfig.VERSION_NAME);
        openDownloadList = (LinearLayout) view.findViewById(R.id.open_downloadlist_page);

        lvAttachTag = view.findViewById(R.id.lv_attach_tag);
        lvDeleteTag = view.findViewById(R.id.lv_delete_tag);
        btnGetAllTag = view.findViewById(R.id.get_tag_btn);
        btnGetAllTag = view.findViewById(R.id.get_tag_btn);


        tagListView = view.findViewById(R.id.tag_list);
        cloudMsgTagAdapter = new CloudMsgTagAdapter(getContext(), tags, R.layout.param_detail_list_item);
        tagListView.setAdapter(cloudMsgTagAdapter);

        checkUpdate = (LinearLayout) view.findViewById(R.id.check_update);
        lvGetLastSuccess = (LinearLayout) view.findViewById(R.id.lv_get_last_success);



        lvRetrieveData = (LinearLayout) view.findViewById(R.id.lv_retrieve_data);
        lvChildRetrieve = (LinearLayout) view.findViewById(R.id.lv_childs_retrieve);
        mImgRetrieve = (ImageView) view.findViewById(R.id.img_retrieve_data);
        getTerminalLocation = (Button) view.findViewById(R.id.get_location);
        getMerchantInfo = (Button) view.findViewById(R.id.get_merchant_info);
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
                //if the market don't have this app, it will show app not found, else will go to detail page in STORE client market
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
                // check if update available from app STORE.
                showProgress();
                Thread thread =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final UpdateObject updateObject = StoreSdk.getInstance().updateApi().checkUpdate(BuildConfig.VERSION_CODE, getContext().getPackageName());
                            LauncherActivity.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
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
                            showNotInitToast();
                        }
                        dismissLoadingDialog();
                    }
                }) ;

                thread.start();

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
                                        final SdkObject sdkObject = StoreSdk.getInstance().cloudMessageApi().attachMsgTag(Arrays.asList(tags.split(",")));
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
                                        showNotInitToast();
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
                                        final SdkObject sdkObject = StoreSdk.getInstance().cloudMessageApi().detachMsgTag(Arrays.asList(tags.split(",")));
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
                                        showNotInitToast();
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
                            final MsgTagObject msgTagObject = StoreSdk.getInstance().cloudMessageApi().getAllTag();
                            LauncherActivity.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (msgTagObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                                        if(msgTagObject.getTags()!= null && !msgTagObject.getTags().isEmpty()) {
                                            cloudMsgTagAdapter.loadData(msgTagObject.getTags());
                                        } else {
                                            Toast.makeText(getContext(), "Tags are empty", Toast.LENGTH_SHORT).show();
                                            cloudMsgTagAdapter.loadData(new ArrayList<String>());
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "Get tags failed:" + msgTagObject.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (NotInitException e) {
                            Log.e(TAG, "e:" + e);
                            showNotInitToast();
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

        lvGetLastSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                Thread thread =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String saveFilePath = getActivity().getFilesDir() + "/YourPath/";
                            DownloadResultObject downloadResultObject = StoreSdk.getInstance().paramApi().downloadLastSuccessParamToPath(saveFilePath);
                            LauncherActivity.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    String msg = "";
                                    if (downloadResultObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                                        msg = "download last success param  Result >> code: " + downloadResultObject.getBusinessCode()
                                                + " >> message: " + downloadResultObject.getMessage();
                                    } else {
                                        msg = "download last success param  Failed >> code: " + downloadResultObject.getBusinessCode()
                                                + " >> message: " + downloadResultObject.getMessage();
                                    }
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (NotInitException e) {
                            Log.e(TAG, "e:" + e);
                            showNotInitToast();
                        }
                        dismissLoadingDialog();
                    }
                }) ;
                thread.start();
            }
        });

        getTerminalLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                Thread thread =  new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final LocationObject locationObject = StoreSdk.getInstance().syncApi().getLocate();
                            Log.d(TAG, "Get Location Result：:" + locationObject.toString());
                            LauncherActivity.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    String msg = "";
                                    if (locationObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                                        msg = "Get Location Result:" + locationObject;
                                    } else {
                                        msg = "Get Location Failed: " + locationObject;
                                    }
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (NotInitException e) {
                            Log.e(TAG, "e:" + e);
                            showNotInitToast();
                        }
                        dismissLoadingDialog();
                    }
                }) ;
                thread.start();
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

        getMerchantInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final MerchantObject merchantObject = StoreSdk.getInstance().syncApi().getMerchantInfo();
                            Log.d(TAG, "Get Merchant Result：:" + merchantObject.toString());
                            LauncherActivity.getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    String msg = "";
                                    if (merchantObject.getBusinessCode() == ResultCode.SUCCESS.getCode()) {
                                        msg = "Get Merchant Result:" + merchantObject;
                                    } else {
                                        msg = "Get Merchant Failed: " + merchantObject;
                                    }
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (NotInitException e) {
                            showNotInitToast();
                            Log.e(TAG, "e: " + e);
                        }
                        dismissLoadingDialog();
                    }
                }) ;
                thread.start();
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

    private void showNotInitToast() {
        LauncherActivity.getHandler().post(new Runnable() {
            @Override
            public void run() {
                String msg = "Not Init";
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
