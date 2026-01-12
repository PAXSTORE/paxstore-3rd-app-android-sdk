package com.pax.android.demoapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {

    private final Context context;
    private final CharSequence text;
    private final int duration;
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    // 私有构造，模仿原生 Toast
    private CustomToast(Context context, CharSequence text, int duration) {
        this.context = context;
        this.text = text;
        this.duration = duration;
    }

    /**
     * 静态工厂方法
     */
    public static CustomToast makeText(Context context, CharSequence text, int duration) {
        return new CustomToast(context, text, duration);
    }

    /**
     * 核心显示逻辑
     */
    public void show() {
        // 必须在主线程 UI 操作
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(this::show);
            return;
        }

        // 只有 Context 是 Activity 时才能向界面添加自定义 View
        if (!(context instanceof Activity)) {
            return;
        }

        final Activity activity = (Activity) context;
        if (activity.isFinishing() || activity.isDestroyed()) return;

        // 1. 获取根布局
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();

        // 2. 创建并配置 TextView (模仿原生 Toast 外观)
        final TextView toastView = new TextView(activity);
        toastView.setText(text);
        toastView.setTextColor(Color.WHITE);
        toastView.setTextSize(14);
        toastView.setLineSpacing(0, 1.2f); // 解决 Android 14 文本拥挤

        // 设置背景（深灰色圆角）
        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.parseColor("#E6323232")); // 约 90% 透明度的深灰
        background.setCornerRadius(dp2px(activity, 24));
        toastView.setBackground(background);

        int hPadding = dp2px(activity, 20);
        int vPadding = dp2px(activity, 12);
        toastView.setPadding(hPadding, vPadding, hPadding, vPadding);

        // 3. 布局参数：底部居中
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.bottomMargin = dp2px(activity, 100);

        // 限制最大宽度，防止超长文本铺满全屏
        toastView.setMaxWidth(activity.getResources().getDisplayMetrics().widthPixels - dp2px(activity, 64));

        // 4. 添加到界面并执行动画
        decorView.addView(toastView, params);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(250);
        toastView.startAnimation(fadeIn);

        // 5. 根据原生时长常量计算实际毫秒数
        // Toast.LENGTH_LONG 大约 3.5s, LENGTH_SHORT 大约 2s
        long delay = (duration == Toast.LENGTH_LONG) ? 3500 : 2000;

        mainHandler.postDelayed(() -> {
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(250);
            toastView.startAnimation(fadeOut);

            mainHandler.postDelayed(() -> {
                if (toastView.getParent() != null) {
                    decorView.removeView(toastView);
                }
            }, 250);
        }, delay);
    }

    private int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}