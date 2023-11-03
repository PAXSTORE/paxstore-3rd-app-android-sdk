# PAXSTORE CloudMessage Integration

By integrating with this function, developers can dilivery message to their application at a specail target device or all devices that installed their application.
### 1.Initialization of Sdk
Refer to the [SetUp](../README.md)

### 2.Add meta-data to AndroidManifest
         <!-- Add below meta-data to support Cloud Message -->
         <meta-data android:name="PAXVAS_CloudMessage"
                    android:value="true"/>

### 3.Register a receiver to recieve message from PAXSTORE.
See the sample [PushMessageReceiver](../demo/src/main/java/com/pax/android/demoapp/PushMessageReceiver.java)

 	    <receiver android:name=".PushMessageReceiver"
                  android:enabled="true"
                  android:exported="false">
            <intent-filter>
                <action android:name="com.paxstore.mpush.NOTIFY_DATA_MESSAGE_RECEIVED" />
                <action android:name="com.paxstore.mpush.DATA_MESSAGE_RECEIVED" />
                <action android:name="com.paxstore.mpush.NOTIFICATION_MESSAGE_RECEIVED" />
                <action android:name="com.paxstore.mpush.NOTIFY_MEDIA_MESSAGE_RECEIVED" />            
                <action android:name="com.paxstore.mpush.NOTIFICATION_CLICK" />
                <category android:name="${applicationId}" />
            </intent-filter>
        </receiver>


### 4.Understand the five actions that the receiver will receive.
##### 4.1.  ACTION_NOTIFY_DATA_MESSAGE_RECEIVED:  
For this action you can get three types of data, title of the notification, content of the notification, jsonString sent to terminal that you can do logic with.
##### 4.2. ACTION_DATA_MESSAGE_RECEIVED：
Only content jsonString you will receive.
##### 4.3. ACTION_NOTIFICATION_MESSAGE_RECEIVED: 
Only title of the notification, content of the notification you will receive.
##### 4.4. ACTION_NOTIFICATION_CLICK:
You will also get notified while the notification clicked by user, and you can retreive data that you have sent to your application.
##### 4.5. ACTION_NOTIFY_MEDIA_MESSAGE_RECEIVED:
Only media jsonString you will receive. You can choose when to show the media message.

### 5.Media Message
For media message, we only store the last one pushed from server. Your application will immediately notice the message if your receiver registered
ACTION_NOTIFY_MEDIA_MESSAGE_RECEIVED. You can choose to show the media message immediately or later. Media Message can be
shown as you previewed in the web through below api. Or you can get media message details to show your own customized dialog.

##### 5.1. Call below api to show the media message as advertisement.

        int showResult = AdvertisementDialog.show(context, false, new AdvertisementDialog.OnLinkClick() {
            @Override
            public void onLinkClick(String linkUrl) {
                clickedLink = true;
                Toast.makeText(MainActivity.this, linkUrl, Toast.LENGTH_SHORT).show();
            }
        });

##### 5.2. Get media message that stored in local.

        StoreSdk.getInstance().getMediaMesage(context);

##### 5.3. Know more about AdvertisementDialog
See [AdvertisementDialog](AdvertisementDialog.md)

##### 5.4. Customize the notification
        //The sdk will show notification for Cloud Messag, you can customized it.
              Notifications.I.init(getApplicationContext())
                .setSmallIcon(R.drawable.logo_demo_white)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.logo_demo)) // If it is Android 12 or above, this will not take effect
                .setOnlyAlertOnce(false)  // Set this flag if you would only like the sound, vibrate and ticker to be played if the notification is not already showing.
                .setAutoCancel(true)      // Setting this flag will make it so the notification is automatically canceled when the user clicks it in the panel. The PendingIntent set with setDeleteIntent will be broadcast when the notification is canceled
                .setCustomContentView(yourCustomContentView)  // Supply a custom RemoteViews to use instead of the standard one.If it's Android 12 or above, this is the folded notification view
                .setCustomBigContentView(yourCustomBigContentView); // Supply a custom RemoteViews to use instead of the expanded view(This is the expansion notification view for Android 12 and above)
    
        //Or you can disable it.
        Notifications.I.setEnabled(false);



### 6.Additional Requirement
1. PAXSTORE client version 7.0+
2. Market subscribes the Cloud Message service.
