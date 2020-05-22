# AdvertisementDialog

com.pax.market.android.app.sdk.AdvertisementDialog, extends Dialog

### Show dialog with media message from push

        int showResult = AdvertisementDialog.show(context, false, new AdvertisementDialog.OnLinkClick() {
            @Override
            public void onLinkClick(String linkUrl) {
                clickedLink = true;
                Toast.makeText(MainActivity.this, linkUrl, Toast.LENGTH_SHORT).show();
            }
        });

| Parameter | Type                   | Description                    |
| --------- | ---------------------- | ------------------------------ |
| context   | Context                | Context                        |
| openLink  | boolean                | true: open link by browser in the terminal when clicking link button. false: do nothing |
| listener  | OnLinkClick            | return the linkUrl when clicking link button |

| Result Code | Description                                                  |
| ----------- | ------------------------------------------------------------ |
| 0           | Success                                                      |
| 1           | Dialog is showing, you can show only one dialog at a time    |
| 2           | No media message were received                               |


### Show dialog with your own data
        MediaMesageInfo mediaMesageInfo = new MediaMesageInfo();
        AdvertisementDialog dialog = AdvertisementDialog.newBuilder().context(context)
                .template(mediaMesageInfo.getTemplate())
                .imgUrl(mediaMesageInfo.getImgUrl())
                .title(mediaMesageInfo.getTitle())
                .titleColor(mediaMesageInfo.getTitleColor())
                .showLink(mediaMesageInfo.isShowLink())
                .linkTextColor(mediaMesageInfo.getLinkTextColor())
                .linkTextBgColor(mediaMessageInfo.getLinkTextBgColor())            
                .linkText(mediaMesageInfo.getLinkText())
                .linkUrl(mediaMesageInfo.getLinkUrl())
                .showSkipButton(mediaMesageInfo.getShowSkipButton())
                .skipButtonText(mediaMesageInfo.getSkipButtonText())
                .openLink(true)
                .countDownTime(mediaMesageInfo.getCountDownTime())            
                .build();
        dialog.show();


| Parameter       | Type                   | Description                    |
| ---------       | ---------------------- | ------------------------------ |
| context         | Context                | Context                        |
| template        | boolean                | 0: Fullscreen 1: Pop-up 2：Pop-up with title |
| imgUrl          | String                 | The url of the img             |
| title           | String                 | Title of dialog (template 2 only) |
| titleColor      | String                 | The color of the title (template 2 only) |
| showLink        | boolean                | Show link button or not        |
| linkTextColor   | String                 | The color of the link text     |
| linkTextBgColor | String                 | The color of the link text background  |
| linkText        | String                 | The text of the link           |
| linkUrl         | String                 | The url of the link            |
| showSkipButton  | boolean                | Show skip button or not (template 0 only) |
| skipButtonText  | String                 | The text of the skip button  (template 0 only) |
| openLink        | String                 | If open link with default browser |
| countDownTime   | String                 | Count down time of the dialog auto closing (template 0 only) |
| listener        | OnLinkClick            | return the linkUrl when clicking link button |

| Result Code | Description                                                  |
| ----------- | ------------------------------------------------------------ |
| 0           | Success                                                      |
| 1           | Dialog is showing, you can show only one dialog at a time    |
| 2           | No media message were received                               |
