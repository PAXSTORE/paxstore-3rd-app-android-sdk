package com.pax.market.android.app.sdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.pax.market.android.app.sdk.dto.MediaMesageInfo;
import com.pax.market.android.app.sdk.util.ImageUtil;
import com.pax.market.android.app.sdk.util.PreferencesUtils;

import java.io.IOException;

public class AdvertisementMidDialog {

    private static final int DEFAULT_COUNT_DOWN = 6;
    private static final int DEFAULT_COLOR = 255;
    View layout;
    WindowManager mWindowManager;
    WindowManager.LayoutParams params;
    /**
     * CountDownTimer 实现倒计时
     */
    private CountDownTimer countDownTimer;
    private Context context;
    private String moreMessage;
    private String moreLink;
    private boolean showSkip;
    private int countDownTime;
    private String imgUrl;
    private String color;
    private View.OnClickListener moreDetailListener;
    private ImageView mImg;
    private TextView mTvCountDown;
    public AdvertisementMidDialog(Builder builder, Context context) {
        this.context = builder.context;
        this.moreMessage = builder.moreMessage;
        this.moreLink = builder.moreLink;
        this.moreDetailListener = builder.moreDetailListener;
        this.showSkip = builder.showSkip;
        this.countDownTime = builder.countDownTime;
        this.imgUrl = builder.imgUrl;
        this.color = builder.color;
        init();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void init() {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.format = PixelFormat.TRANSPARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // instantiate the dialog with the custom Theme
        layout = inflater.inflate(R.layout.dialog_advertisement_full, null);
        mImg = layout.findViewById(R.id.img_adver);

        // set the dialog title
        if (showSkip) {
            layout.findViewById(R.id.tv_count_down)
                    .setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
        }
        if (moreMessage != null) {
            ((TextView) layout.findViewById(R.id.tv_show_more)).setText(moreMessage);
            layout.findViewById(R.id.tv_show_more).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moreDetailListener.onClick(view);
                    dismiss();
                }
            });
        }
        mTvCountDown = (TextView) layout.findViewById(R.id.tv_count_down);
        MediaMesageInfo mediaMesageInfo = PreferencesUtils.getObject(context, PushConstants.MEDIA_MESSAGE_FULL, MediaMesageInfo.class);
        if (mediaMesageInfo.getSavedPath() != null) {
            Bitmap bitmapFromFile = ImageUtil.getBitmapFromFile(context.getFilesDir() + CommonConstants.MEDIA_PATH_FULL, context);
            if (bitmapFromFile == null) { // If local does not exist, load from url
                ImageLoadTask imageLoadTask = new ImageLoadTask();
                imageLoadTask.execute(imgUrl);
            } else {
                mImg.setImageBitmap(bitmapFromFile);
            }
        } else {
            ImageLoadTask imageLoadTask = new ImageLoadTask();
            imageLoadTask.execute(imgUrl);
        }


        if (color != null) {
            int parseColor = DEFAULT_COLOR;
            try {
                parseColor = Color.parseColor(color);
            } catch (IllegalArgumentException e) {
                Log.e("AdvertisementDialog", "e:" + e);
            }
            ((TextView) layout.findViewById(R.id.tv_show_more)).setTextColor(parseColor);
        }
        mWindowManager.addView(layout, params);
        startTimeout();
    }

    public void dismiss() {
        if (mWindowManager != null && layout != null) {
            mWindowManager.removeView(layout);
            layout = null;
            context = null;
            stopTimeout();
        }
    }

    private void startTimeout() {
        countDownTimer = new CountDownTimer(countDownTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int time = (int) (millisUntilFinished / 1000);
                mTvCountDown.setText((showSkip ? "Skip " : "") + time + "S");
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
        countDownTimer.cancel();
    }

    public static class Builder {
        private Context context;
        private String moreMessage;
        private String moreLink;
        private boolean showSkip;
        private int countDownTime = DEFAULT_COUNT_DOWN;
        private String imgUrl;
        private String color;
        private View.OnClickListener moreDetailListener;

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder moreMessage(String moreMessage, View.OnClickListener listener) {
            this.moreMessage = moreMessage;
            this.moreDetailListener = listener;
            return this;
        }

        public Builder moreLink(String moreLink) {
            this.moreLink = moreLink;
            return this;
        }

        public Builder showSkip(boolean showSkip) {
            this.showSkip = showSkip;
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

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public AdvertisementMidDialog build() {
            if (this.context == null) {
                throw new NullPointerException("Activity can not be NULL!!");
            }
            MediaMesageInfo mediaMesageInfo = PreferencesUtils.getObject(context, PushConstants.MEDIA_MESSAGE_FULL, MediaMesageInfo.class);
            if (mediaMesageInfo == null) {// todo
                throw new NullPointerException("No media message of full screen");
            }
            return new AdvertisementMidDialog(this, context);
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
                    String savePath = context.getFilesDir() + CommonConstants.MEDIA_PATH_FULL;
                    boolean saveResult = ImageUtil.saveImage(bitmapFromUrl, savePath);
                    if (saveResult) {
                        MediaMesageInfo mediaMesageInfo = PreferencesUtils.getObject(context, PushConstants.MEDIA_MESSAGE_FULL, MediaMesageInfo.class);
                        mediaMesageInfo.setSavedPath(savePath);
                        PreferencesUtils.putObject(context, PushConstants.MEDIA_MESSAGE_FULL, mediaMesageInfo);
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
        }

        //取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {

        }
    }
}
