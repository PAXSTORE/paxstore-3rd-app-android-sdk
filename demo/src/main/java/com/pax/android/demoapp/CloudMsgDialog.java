/*
 * ******************************************************************************
 * COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *
 *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 * ******************************************************************************
 */

package com.pax.android.demoapp;

/**
 * Created by zcy on 2016/8/3 0003.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class CloudMsgDialog extends Dialog {
    private Context context;
    private String title;
    private ClickListener clickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface ClickListener{
        void onClick(String tags);
    }

    public CloudMsgDialog(Builder builder, Context context) {
        super(context, R.style.Dialog_Fullscreen);
        setCanceledOnTouchOutside(true);
        this.context = builder.context;
        this.title = builder.title;
        this.clickListener = builder.clickListener;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.cloud_msg_dialog, null);
        TextView tvTitle = layout.findViewById(R.id.tv_title);
        final EditText tvTags = layout.findViewById(R.id.tag_text);
        tvTitle.setText(title);

        layout.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvTags.getText() != null) {
                    clickListener.onClick(tvTags.getText().toString());
                }
                dismiss();
            }
        });

        setContentView(layout);
    }

    public static class Builder {
        private Context context;
        private String title;
        private ClickListener clickListener;


        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder listener(ClickListener clickListener) {
            this.clickListener = clickListener;
            return this;
        }

        public CloudMsgDialog build() {
            if (this.context == null) {
                throw new NullPointerException("Activity can not be NULL!!");
            }
            return new CloudMsgDialog(this, context);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}