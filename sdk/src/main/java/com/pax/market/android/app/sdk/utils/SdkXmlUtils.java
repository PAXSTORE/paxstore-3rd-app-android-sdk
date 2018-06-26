package com.pax.market.android.app.sdk.utils;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by wing on 2018/6/25.
 */
public class SdkXmlUtils {

    public static final String HEAD_TAG = "parameter";

    public static HashMap<String,String> parseDownloadParamXml(InputStream is) throws XmlPullParserException, IOException {
        HashMap<String,String> resultMap = null;
        if(is!=null){
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser =  factory.newPullParser();
            parser.setInput(is, "utf-8");
            int type = parser.getEventType();
            boolean headTag = true;
            while (type != XmlPullParser.END_DOCUMENT) {
                if(type == parser.START_DOCUMENT) {
                    resultMap = new HashMap<>();
                }else if(type == parser.START_TAG) {
                    if (HEAD_TAG.equalsIgnoreCase(parser.getName()) && headTag) {
                        //the download parameter xml default to start with <parameter> tag, here to ignore the head tag.
                        headTag=false;
                    } else {
                        //put the other tag into result map
                        String key = parser.getName();
                        parser.next();
                        String value = parser.getText();
                        resultMap.put(key,value);
                    }
                }
                type = parser.next();
            }
        }
        return resultMap;
    }
}
