package com.pax.market.android.app.sdk.dto;

import android.os.CountDownTimer;

public class MediaMesageInfo {

    private String savedPath;
    private int template;
    private CountDownTimer countDownTimer;
    private String linkUrl;
    private boolean showSkipButton;
    private Integer countDownTime;
    private String imgUrl;
    private String linkTextColor;
    private String linkTextBgColor;
    private String linkText;
    private String skipButtonText;
    private boolean showLink;
    private String title;
    private String titleColor;


    public String getLinkTextBgColor() {
        return linkTextBgColor;
    }

    public void setLinkTextBgColor(String linkTextBgColor) {
        this.linkTextBgColor = linkTextBgColor;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getSkipButtonText() {
        return skipButtonText;
    }

    public void setSkipButtonText(String skipButtonText) {
        this.skipButtonText = skipButtonText;
    }

    public boolean isShowLink() {
        return showLink;
    }

    public void setShowLink(boolean showLink) {
        this.showLink = showLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public int getTemplate() {
        return template;
    }

    public void setTemplate(int template) {
        this.template = template;
    }

    public CountDownTimer getCountDownTimer() {
        return countDownTimer;
    }

    public void setCountDownTimer(CountDownTimer countDownTimer) {
        this.countDownTimer = countDownTimer;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public boolean getShowSkipButton() {
        return showSkipButton;
    }

    public void setShowSkipButton(Boolean showSkipButton) {
        this.showSkipButton = showSkipButton;
    }

    public Integer getCountDownTime() {
        return countDownTime;
    }

    public void setCountDownTime(Integer countDownTime) {
        this.countDownTime = countDownTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLinkTextColor() {
        return linkTextColor;
    }

    public void setLinkTextColor(String linkTextColor) {
        this.linkTextColor = linkTextColor;
    }

    public String getSavedPath() {
        return savedPath;
    }

    public void setSavedPath(String savedPath) {
        this.savedPath = savedPath;
    }

    @Override
    public String toString() {
        return "MediaMesageInfo{" +
                "savedPath='" + savedPath + '\'' +
                ", template=" + template +
                ", countDownTimer=" + countDownTimer +
                ", linkUrl='" + linkUrl + '\'' +
                ", showSkipButton=" + showSkipButton +
                ", countDownTime=" + countDownTime +
                ", imgUrl='" + imgUrl + '\'' +
                ", linkTextColor='" + linkTextColor + '\'' +
                ", linkTextBgColor='" + linkTextBgColor + '\'' +
                ", linkText='" + linkText + '\'' +
                ", skipButtonText=" + skipButtonText +
                ", showLink=" + showLink +
                ", title='" + title + '\'' +
                ", titleColor='" + titleColor + '\'' +
                '}';
    }
}
