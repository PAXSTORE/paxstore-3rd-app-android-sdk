package com.pax.market.android.app.sdk;

/**
 * Created by fojut on 2019/5/17.
 */

public class NotificationMessage implements PushMessage {

    private String title;
    private String content;

    @Override
    public Integer getNid() {
        return null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
