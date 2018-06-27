/*
 *  *******************************************************************************
 *  COPYRIGHT
 *                PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *    This software is supplied under the terms of a license agreement or
 *    nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *    or disclosed except in accordance with the terms in that agreement.
 *
 *       Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 *  *******************************************************************************
 */
package com.pax.market.api.sdk.java.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zcy on 2017/4/17 0017.
 */
public class Md5Utils {
    /**
     * 获取单个文件的MD5值！

     * @param file
     * @return
     */

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            String md5 = getFileMD5(in);

            return md5;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(in != null){
                try{
                    in.close();
                }catch (Exception ignore){

                }
            }
        }
    }

    public static String getFileMD5(InputStream fis) throws Exception {
        MessageDigest md = null;
        md = MessageDigest.getInstance("MD5");


        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }

        byte[] mdbytes = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
            String hex = Integer.toHexString(0xff & mdbytes[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String result = hexString.toString();
        return result;
    }

    /**
     * 获取文件夹中文件的MD5值
     *
     * @param file
     * @param listChild
     *            ;true递归子目录中的文件
     * @return
     */
    public static Map<String, String> getDirMD5(File file, boolean listChild) {
        if (!file.isDirectory()) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        String md5;
        File files[] = file.listFiles();
        for (File f : files != null ? files : new File[0]) {
            if (f.isDirectory() && listChild) {
                map.putAll(getDirMD5(f, listChild));
            } else {
                md5 = getFileMD5(f);
                if (md5 != null) {
                    map.put(f.getPath(), md5);
                }
            }
        }
        return map;
    }

    /**
     * 用于获取一个String的md5值
     * @param str
     * @return
     */
    public static String getMd5(String str) {
        MessageDigest digest = null;
        StringBuilder sb = new StringBuilder(40);
        try {
            digest = MessageDigest.getInstance("MD5");//NOSONAR
            byte[] bs = digest.digest(str.getBytes());
            for (byte x : bs) {
                if ((x & 0xff) >> 4 == 0) {
                    sb.append("0").append(Integer.toHexString(x & 0xff));
                } else {
                    sb.append(Integer.toHexString(x & 0xff));
                }
            }
        } catch (Exception ex) {

        }
        return sb.toString();
    }
}
