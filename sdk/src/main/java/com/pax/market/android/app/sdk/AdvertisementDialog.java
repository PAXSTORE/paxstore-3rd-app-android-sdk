package com.pax.market.android.app.sdk;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pax.market.android.app.sdk.dto.MediaMesageInfo;
import com.pax.market.android.app.sdk.util.ImageUtil;
import com.pax.market.android.app.sdk.util.PreferencesUtils;

import java.io.IOException;

public class AdvertisementDialog extends Dialog {
    /**
     * The dialog is showing
     */
    private static final int ERR_DIALOG_SHOWING = 1;
    /**
     * There has been no media message sent from server.
     */
    private static final int ERR_NO_DATA = 2;

    private static final int SUCCESS = 0;

    private static final int DEFAULT_COUNT_DOWN = 5;
    private static final int DEFAULT_COLOR = 255;
    private static AdvertisementDialog instance;

    View layout;
    WindowManager mWindowManager;
    WindowManager.LayoutParams params;
    /**
     * CountDownTimer 实现倒计时
     */
    private CountDownTimer countDownTimer;
    private Context context;
    private int template = PushConstants.MEDIA_TYPE_FULL;
    private String linkText;
    private String linkUrl;
    private boolean showSkipButton;
    private int countDownTime = DEFAULT_COUNT_DOWN;
    private String imgUrl;
    private String linkTextColor;
    private String linkTextBgColor;
    private String skipButtonText;
    private boolean showLink;
    private String title;
    private String titleColor;
    private OnLinkClick listener;
    private boolean openLink = true;


    private ImageView mImg;
    private TextView mTvCountDown, mtvTitle;
    private LinearLayout mLvMore;

    public AdvertisementDialog(Builder builder, Context context) {
        super(context, R.style.Dialog);
        setOwnerActivity((Activity) context);
        this.template = builder.template;
        this.context = builder.context;
        this.linkText = builder.linkText;
        this.linkUrl = builder.linkUrl;
        this.listener = builder.moreDetailListener;
        this.showSkipButton = builder.showSkipButton;
        this.countDownTime = builder.countDownTime;
        this.imgUrl = builder.imgUrl;
        this.linkTextColor = builder.linkTextColor;
        this.linkTextBgColor = builder.linkTextBgColor;
        this.skipButtonText = builder.skipButtonText;
        this.showLink = builder.showLink;
        this.title = builder.title;
        this.titleColor = builder.titleColor;
        this.openLink = builder.openLink;

        init();
    }

    public interface OnLinkClick {
        void onLinkClick(String linkUrl);
    }



    public static Builder newBuilder() {
        return new Builder();
    }

    public static int show(Context context, OnLinkClick listener) {
        return show(context, true, listener);
    }

    public static int show(Context context, boolean openLink, OnLinkClick listener) {
        return new Builder().context(context).openLink(openLink).showDialog(listener);
    }

    public void init() {

        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // instantiate the dialog with the custom Theme
        if (template == PushConstants.MEDIA_TYPE_FULL) {
            layout = inflater.inflate(R.layout.dialog_advertisement_full, null);
            if (showSkipButton) {
                TextView tvSkip = layout.findViewById(R.id.tv_count_down);
                if (skipButtonText != null) {
                    tvSkip.setText(skipButtonText);
                }
                tvSkip.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dismiss();
                    }
                });
            }
            mTvCountDown = (TextView) layout.findViewById(R.id.tv_count_down);
        } else if (template == PushConstants.MEDIA_TYPE_MID) {
            layout = inflater.inflate(R.layout.dialog_advertisement_mid, null);
            layout.findViewById(R.id.img_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        } else if (template == PushConstants.MEDIA_TYPE_TITLE) {
            layout = inflater.inflate(R.layout.dialog_advertisement_title, null);
            mtvTitle = layout.findViewById(R.id.tv_title);
            mtvTitle.setText(title);
            mtvTitle.setTextColor(getParseColor(titleColor));
            layout.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
            if (linkTextBgColor != null) {
                int parseColor = getParseColor(linkTextBgColor);
                LinearLayout lvMore = layout.findViewById(R.id.lv_more);
                lvMore.setBackgroundColor(parseColor);
            }
        }
        addContentView(layout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mImg = layout.findViewById(R.id.img_adver);
        mLvMore = layout.findViewById(R.id.lv_more);

        // set the dialog title
        if (showLink) {
            mLvMore.setVisibility(View.VISIBLE);
            ((TextView) layout.findViewById(R.id.tv_show_more)).setText(linkText);
            layout.findViewById(R.id.tv_show_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onLinkClick(linkUrl);
                    }
                    dismiss();
                    if (openLink) { // Users can choose open link by themselves.
                        openLink();
                    }
                }
            });
            if (linkTextColor != null) {
                int parseColor = DEFAULT_COLOR;
                try {
                    parseColor = Color.parseColor(linkTextColor);
                } catch (IllegalArgumentException e) {
                    Log.e("AdvertisementDialog", "e:" + e);
                }
                ((TextView) layout.findViewById(R.id.tv_show_more)).setTextColor(parseColor);
            }
        } else {
            mLvMore.setVisibility(View.GONE);
        }

        MediaMesageInfo mediaMesageInfo = PreferencesUtils.getObject(context, PushConstants.MEDIA_MESSAGE, MediaMesageInfo.class);
        if (mediaMesageInfo.getSavedPath() != null) {
            Bitmap bitmapFromFile = ImageUtil.getBitmapFromFile(context.getFilesDir() + CommonConstants.MEDIA_PATH, context);
            if (bitmapFromFile == null) { // If local does not exist, load from url
                ImageLoadTask imageLoadTask = new ImageLoadTask();
                imageLoadTask.execute(imgUrl);
            } else {
                mImg.setImageBitmap(bitmapFromFile);
                if (template == PushConstants.MEDIA_TYPE_FULL) {
                    startTimeout(skipButtonText);
                }
            }
        } else {
            ImageLoadTask imageLoadTask = new ImageLoadTask();
            imageLoadTask.execute(imgUrl);
        }


        setContentView(layout);
    }

    private void openLink() {
        if (linkUrl == null) {
            Toast.makeText(context, "Link url is null", Toast.LENGTH_LONG).show();
            return;
        }
        final Uri uri = Uri.parse(linkUrl);
        final Intent it = new Intent(Intent.ACTION_VIEW, uri);
        try {
            ((Activity) context).startActivityForResult(it, 11);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Error: there is no browser found to open this link.", Toast.LENGTH_LONG).show();
            Log.e("AdvertisementDialog", "e:" + e);
        }
    }


    private int getParseColor(String colorStr) {
        int parseColor = DEFAULT_COLOR;
        if (titleColor != null) {
            try {
                parseColor = Color.parseColor(colorStr);
            } catch (IllegalArgumentException e) {
                Log.e("AdvertisementDialog", "e:" + e);
            }
        }
        return parseColor;
    }

    private void startTimeout(final String skipButtonText) {
        mTvCountDown.setText((skipButtonText == null ? "" : skipButtonText + " ") + countDownTime + "S");
        countDownTimer = new CountDownTimer(countDownTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int time = (int) (millisUntilFinished / 1000);
                mTvCountDown.setText((skipButtonText == null ? "" : skipButtonText + " ") + time + "S");
            }

            @Override
            public void onFinish() {
                Log.d("AdvertisementFull", "onFinish");
                dismiss();
            }
        };
        countDownTimer.start();
    }

    private void stopTimeout() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public static class Builder {
        private Context context;
        private OnLinkClick moreDetailListener;
        private int template = PushConstants.MEDIA_TYPE_FULL;
        private String linkText;
        private String linkUrl;
        private boolean showSkipButton;
        private int countDownTime = DEFAULT_COUNT_DOWN;
        private String imgUrl;
        private String linkTextColor;
        private String linkTextBgColor;
        private String skipButtonText;
        private boolean showLink;
        private String title;
        private String titleColor;
        private boolean openLink = true;

        public Builder openLink(boolean openLink) {
            this.openLink = openLink;
            return this;
        }

        public Builder linkTextBgColor(String linkTextBgColor) {
            this.linkTextBgColor = linkTextBgColor;
            return this;
        }

        public Builder skipButtonText(String skipButtonText) {
            this.skipButtonText = skipButtonText;
            return this;
        }

        public Builder showLink(boolean showLink) {
            this.showLink = showLink;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder titleColor(String titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder template(int template) {
            this.template = template;
            return this;
        }

        public Builder linkText(String linkText) {
            this.linkText = linkText;
            return this;
        }

        public Builder linkListener(OnLinkClick moreDetailListener) {
            this.moreDetailListener = moreDetailListener;
            return this;
        }

        public Builder linkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
            return this;
        }

        public Builder showSkipButton(boolean showSkipButton) {
            this.showSkipButton = showSkipButton;
            return this;
        }

        public Builder countDownTime(int countDownTime) {
            this.countDownTime = countDownTime;
            return this;
        }

        public Builder imgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
            return this;
        }

        public Builder linkTextColor(String linkTextColor) {
            this.linkTextColor = linkTextColor;
            return this;
        }

        /**
         * 通过build方法可以添加 任意多个dialog，用户自己判断
         *
         * @return
         */
        public AdvertisementDialog build() {
            if (this.context == null) {
                throw new NullPointerException("Activity can not be NULL!!");
            }
            AdvertisementDialog advertisementDialog = new AdvertisementDialog(this, context);
            advertisementDialog.setCancelable(false);
            if (template == PushConstants.MEDIA_TYPE_FULL) {
                Window window = advertisementDialog.getWindow();
// 把 DecorView 的默认 padding 取消，同时 DecorView 的默认大小也会取消
                window.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
// 设置宽度
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(layoutParams);
// 给 DecorView 设置背景颜色，很重要，不然导致 Dialog 内容显示不全，有一部分内容会充当 padding，上面例子有举出
                window.getDecorView().setBackgroundColor(Color.GREEN);
            }
            return advertisementDialog;
        }

        public int showDialog(OnLinkClick listener) {
            if (instance != null && instance.isShowing()) {
                Log.w("AdvertisementDialog", "Dialog is showing!");
                return ERR_DIALOG_SHOWING; // 有正在显示的dialog
            }
            if (this.context == null) {
                throw new NullPointerException("Activity can not be NULL!!");
            }
            MediaMesageInfo mediaMesageInfo = getMediaMesageInfo();
            if (mediaMesageInfo == null) {
                return ERR_NO_DATA; // 没有sp值
            }

            setBuilder(mediaMesageInfo, listener);

            AdvertisementDialog advertisementDialog = new AdvertisementDialog(this, context);
            advertisementDialog.setCancelable(false);
            if (template == PushConstants.MEDIA_TYPE_FULL) {
                Window window = advertisementDialog.getWindow();
// 把 DecorView 的默认 padding 取消，同时 DecorView 的默认大小也会取消
                window.getDecorView().setPadding(0, 0, 0, 0);
                WindowManager.LayoutParams layoutParams = window.getAttributes();
// 设置宽度
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(layoutParams);
// 给 DecorView 设置背景颜色，很重要，不然导致 Dialog 内容显示不全，有一部分内容会充当 padding，上面例子有举出
                window.getDecorView().setBackgroundColor(Color.GREEN);
            }
            instance = advertisementDialog;
            instance.show();
            return SUCCESS;
        }

        /**
         * 根据sp里存的message来显示dialog
         * @param mediaMesageInfo
         */
        private void setBuilder(MediaMesageInfo mediaMesageInfo, OnLinkClick moreDetailListener) {
            context(context);
            template(mediaMesageInfo.getTemplate());
            linkText(mediaMesageInfo.getLinkText());
            linkListener(moreDetailListener);
            linkUrl(mediaMesageInfo.getLinkUrl());
            showSkipButton(mediaMesageInfo.getShowSkipButton());
            imgUrl(mediaMesageInfo.getImgUrl());
            linkTextColor(mediaMesageInfo.getLinkTextColor());
            linkTextBgColor(mediaMesageInfo.getLinkTextBgColor());
            skipButtonText(mediaMesageInfo.getSkipButtonText());
            showLink(mediaMesageInfo.isShowLink());
            title(mediaMesageInfo.getTitle());
            titleColor(mediaMesageInfo.getTitleColor());
        }

        private void setDialogValue(Context context) {

        }

        private MediaMesageInfo getMediaMesageInfo() {
            return PreferencesUtils.getObject(context, PushConstants.MEDIA_MESSAGE, MediaMesageInfo.class);
        }
    }


    private class ImageLoadTask extends AsyncTask<String, Integer, Bitmap> {
        //执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
        }

        //执行后台任务（耗时操作）,不可在此方法内修改UI
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                Bitmap bitmapFromUrl = ImageUtil.getBitmapFromUrl(params[0]);
                if (bitmapFromUrl != null) {
                    //更新SP，更新为本地的图片地址
                    String savePath = context.getFilesDir() + CommonConstants.MEDIA_PATH;
                    boolean saveResult = ImageUtil.saveImage(bitmapFromUrl, savePath);
                    if (saveResult) {
                        MediaMesageInfo mediaMesageInfo = PreferencesUtils.getObject(context, PushConstants.MEDIA_MESSAGE, MediaMesageInfo.class);
                        mediaMesageInfo.setSavedPath(savePath);
                        PreferencesUtils.putObject(context, PushConstants.MEDIA_MESSAGE, mediaMesageInfo);
                    }
                }
                return bitmapFromUrl;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        //执行完后台任务后更新UI
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                mImg.setImageBitmap(bitmap);
            } else {
                Log.e("ImageLoadTask", "Get null picture");
            }
            if (template == PushConstants.MEDIA_TYPE_FULL) {
                startTimeout(skipButtonText);
            }
        }

        //取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }
}
