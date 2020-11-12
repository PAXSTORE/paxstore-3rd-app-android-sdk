package com.pax.market.android.app.sdk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pax.market.api.sdk.java.base.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * CloudMessage example:
 * {
 *      "notification":{
 *          "title":"Portugal vs. Denmark",      //Required, if not set, the application name will be used automatically
 *          "content":"great match!"             //Not required
 *      },
 *      "data":{
 *          "nick":"Mario",
 *          "room":"PortugalVSDenmark",
 *          "age":28
 *      }
 *  }
 */
public class CloudMessage {

    private static final Logger logger = LoggerFactory.getLogger(CloudMessage.class);

    private String notificationJson;
    private String dataJson;
    private String mediaJson;

    private static Gson gson;
    public static Gson getGson() {
        if (gson == null) {
            synchronized (CloudMessage.class) {
                if (gson == null) {
                    gson = new GsonBuilder().create();
                }
            }
        }
        return gson;
    }

    private CloudMessage(String notificationJson, String dataJson, String mediaJson) {
        this.notificationJson = notificationJson;
        this.dataJson = dataJson;
        this.mediaJson = mediaJson;
    }

    public static CloudMessage fromJson(String json){
        JsonParser parser = new JsonParser();
        JsonElement rootElement;
        try {
            rootElement = parser.parse(json);
        } catch (Exception e) {
            logger.error("Parse json exception, json=%s", json, e);
            return null;
        }
        String notificationJson = null;
        String dataJson = null;
        String mediaJson = null;
        if (rootElement.isJsonObject()){
            try {
                JsonElement notificationJsonEle = ((JsonObject) rootElement).get(MessageFiled.NOTIFICATION.getName());
                if (notificationJsonEle != null && notificationJsonEle.isJsonObject()) {
                    notificationJson = notificationJsonEle.getAsJsonObject().toString();
                }
            } catch (Exception e) {
                logger.error("Parse notification json exception, rootElement=%s", rootElement, e);
            }
            try {
                JsonElement dataJsonEle = ((JsonObject) rootElement).get(MessageFiled.DATA.getName());
                if (dataJsonEle != null && dataJsonEle.isJsonObject()) {
                    dataJson = dataJsonEle.getAsJsonObject().toString();
                }
            } catch (Exception e) {
                logger.error("Parse data json exception, rootElement=%s", rootElement, e);
            }

            try {
                JsonElement mediaJsonEle = ((JsonObject) rootElement).get(MessageFiled.MEDIA.getName());
                if (mediaJsonEle != null && mediaJsonEle.isJsonObject()) {
                    mediaJson = mediaJsonEle.getAsJsonObject().toString();
                }
            } catch (Exception e) {
                logger.error("Parse data json exception, rootElement=%s", rootElement, e);
            }

        }

        if(StringUtils.isEmpty(notificationJson) && StringUtils.isEmpty(dataJson) && StringUtils.isEmpty(mediaJson)){
            return null;
        }

        return new CloudMessage(notificationJson, dataJson, mediaJson);
    }

    public NotificationMessage getNotification() {
        try {
            return getGson().fromJson(notificationJson, NotificationMessage.class);
        } catch (Exception e) {
            logger.error("Parse notification json exception, json=%s", notificationJson, e);
            return null;
        }
    }

    public String getDataJson() {
        return dataJson;
    }

    public boolean isDataEmpty(){
        return StringUtils.isEmpty(dataJson);
    }

    public String getMediaJson() {
        return mediaJson;
    }

    public boolean isMediaEmpty() {
        return StringUtils.isEmpty(mediaJson);
    }

    public static <T> T getDataFromJson(String dataJson, Class<T> classOfT) {
        try {
            return getGson().fromJson(dataJson, classOfT);
        } catch (Exception e) {
            logger.error("Parse notification json exception, json=%s", dataJson, e);
            return null;
        }
    }

    public static <T> T getDataFromJson(String dataJson, Type typeOfT) {
        try {
            return getGson().fromJson(dataJson, typeOfT);
        } catch (Exception e) {
            logger.error("Parse notification json exception, json=%s", dataJson, e);
            return null;
        }
    }

    public enum MessageFiled {
        NOTIFICATION("notification"),
        DATA("data"),
        MEDIA("media");

        MessageFiled(String name){
            this.name = name;
        }
        private String name;
        public String getName() {
            return name;
        }
    }

    @Override
    public String toString() {
        return "CloudMessage{" +
                "notificationJson='" + notificationJson + '\'' +
                ", dataJson='" + dataJson + '\'' +
                ", mediaJson='" + mediaJson + '\'' +
                '}';
    }
}
