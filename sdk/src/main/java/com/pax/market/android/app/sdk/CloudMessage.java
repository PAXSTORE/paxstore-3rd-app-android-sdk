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
 * CloudMessage示例：
 * {
 *      "notification":{
 *          "title":"Portugal vs. Denmark",      //必填，如果未设置则自动使用应用名称
 *          "content":"great match!"             //非必填
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

    private CloudMessage(String notificationJson, String dataJson) {
        this.notificationJson = notificationJson;
        this.dataJson = dataJson;
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
        }

        if(StringUtils.isEmpty(notificationJson) && StringUtils.isEmpty(dataJson)){
            return null;
        }

        return new CloudMessage(notificationJson, dataJson);
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
        DATA("data");

        MessageFiled(String name){
            this.name = name;
        }
        private String name;
        public String getName() {
            return name;
        }
    }
}
