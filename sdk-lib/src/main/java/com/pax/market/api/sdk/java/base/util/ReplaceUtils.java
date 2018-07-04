package com.pax.market.api.sdk.java.base.util;

import com.google.gson.Gson;
import com.pax.market.api.sdk.java.base.constant.Constants;
import com.pax.market.api.sdk.java.base.dto.ParamsVariableObject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by zhangchenyang on 2018/2/26.
 */
public class ReplaceUtils {


    private static final Logger logger = LoggerFactory.getLogger(ReplaceUtils.class);

    public static boolean replaceParams(String filePath, String paramVariables) {
        List<ParamsVariableObject> paramList = exchangeValues(paramVariables);

        if (paramList!=null && !paramList.isEmpty()) {
            File dic = new File(filePath);
            if (dic != null && dic.isDirectory()) {
                for (File file : dic.listFiles()) {
                    String s = readFile(file);
                    if (s != null) {
                        if (s.startsWith(Constants.XML_FILE_PREFIX)){
                            try {
                                String fullFile = FileUtils.readFileToString(file);
                                String replaceResult = fullFile;
                                for (ParamsVariableObject paramsVariableObject : paramList) {
                                    replaceResult = replaceResult.replaceAll("(?i)" + escapeExprSpecialWord(paramsVariableObject.getKey()),
                                            escapeXml(paramsVariableObject.getValue()));
                                }
                                //rewrite file
                                if (!replaceResult.equals(fullFile)) {
                                    logger.debug(file.getName() + " replaced");
                                    FileUtils.writeStringToFile(file, replaceResult);
                                }
                            } catch (IOException e) {
                                logger.error(" replaceParams failed ", e);
                                return false;
                            }
                        }
                    } else {
                        logger.warn(file.getName() + " is empty, skipped");
                    }
                }
            } else {
                logger.error("Cannot find file folder " + filePath + ">>>> replace paramVariables failed");
                return false;
            }
        }

        return true;
    }

    private static List<ParamsVariableObject> exchangeValues(String json) {
        if (json != null) {
            List<ParamsVariableObject> list = new ArrayList<ParamsVariableObject>();
            Gson gson = new Gson();
            HashMap object = gson.fromJson(json, HashMap.class);
            if (object != null && object.size() > 0) {
                Iterator iterator = object.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    ParamsVariableObject dto = new ParamsVariableObject();
                    dto.setKey((String) entry.getKey());
                    dto.setValue((String)entry.getValue());
                    list.add(dto);
                }
            }
            return list;
        }
        return null;
    }

    private static String readFile(File fin) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fin);
            //Construct BufferedReader from InputStreamReader
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            if ((line = br.readLine()) != null) {
                return line;
            }
            br.close();
        } catch (FileNotFoundException e) {
            logger.error("read file first line failed, file is null", e);
        } catch (IOException e) {
            logger.error("read file first line failed, IOException", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * ignore regex in string
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (!StringUtils.isEmpty(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }



    private static String getEscape(char c) {
        switch (c) {
            case '&':
                return "&amp;";
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '"':
                return "&quot;";
            case '\'':
                return "&apos;";
        }

        return String.valueOf(c);
    }

    public static String escapeXml(String src) {
        if(src == null || src.length() == 0) {
            return src;
        }

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            buf.append(getEscape(src.charAt(i)));
        }
        return Matcher.quoteReplacement(buf.toString());
    }



}
