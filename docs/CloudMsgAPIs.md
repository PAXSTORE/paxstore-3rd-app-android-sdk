# PAXSTORE CLOUDMSG APIS

By integrating with this function, developers can dilivery message to their application at a specail target device or all devices that installed their application.

### 1.Add meta-data to AndroidManifest
         <!-- Add below meta-data to support Cloud Message -->
         <meta-data android:name="PAXVAS_CloudMessage"
                    android:value="true"/>

### 2.Register the receiver to recieve message from PAXSTORE
Since from Android Oreo, we need to send explicit broadcast to third party application.

You are expected to create the same "PushMessageReceiver" as we defined in our sample, and put it under root package. So we can find the receiver by "your packageName + .PushMessageReceiver".

 	    <receiver android:name=".PushMessageReceiver"
                  android:enabled="true"
                  android:exported="false">
            <intent-filter>
                <action android:name="com.paxstore.mpush.NOTIFY_DATA_MESSAGE_RECEIVED" />
                <action android:name="com.paxstore.mpush.DATA_MESSAGE_RECEIVED" />
                <action android:name="com.paxstore.mpush.NOTIFICATION_MESSAGE_RECEIVED" />
                <action android:name="com.paxstore.mpush.NOTIFICATION_CLICK" />
                <category android:name="${applicationId}" />
            </intent-filter>
        </receiver>

### 3.Understand the four actions that the receiver will receive.
##### 3.1.  ACTION_NOTIFY_DATA_MESSAGE_RECEIVED:  
For this action you can get three types of data, title of the notification, content of the notification, jsonString sent to terminal that you can do logic with.
##### 3.2. ACTION_DATA_MESSAGE_RECEIVED：
Only jsonString you will receive.
##### 3.3. ACTION_NOTIFICATION_MESSAGE_RECEIVED: 
Only title of the notification, content of the notification you will receive.
##### 3.4. ACTION_NOTIFICATION_CLICK:
You will also get notified while the notification clicked by user, and you can retreive data that you have sent to your application.


### 4.Requirement
1. PAXSTORE client version 7.0+
2. Market subscribes the Cloud Message service.
