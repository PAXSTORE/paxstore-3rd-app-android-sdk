/*
 * *******************************************************************************
 * COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *
 *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 * *******************************************************************************
 */
package com.pax.market.api.sdk.java.base.util;


import com.pax.market.api.sdk.java.base.constant.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by fanjun on 2016/11/10.
 */
public class CryptoUtils {
    private static final Logger logger = LoggerFactory.getLogger(CryptoUtils.class);

    private static final String AES = "AES";
    private static final String AES_CBC = "AES/CBC/PKCS5Padding";

    public static final int DEFAULT_AES_KEY_SIZE = 128;
    public static final int DEFAULT_IV_SIZE = 16;
    private static SecureRandom random = new SecureRandom();

    private CryptoUtils() {}

    /**
     * 给请求签名。
     *
     * @param params     请求参数
     * @param secret     签名密钥
     * @param signMethod signMethod 签名方法
     * @return 签名 string
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     */
    public static String signRequest(Map<String, String> params, String secret, String signMethod) throws IOException, GeneralSecurityException {
        return signRequest(params, null, secret, signMethod);
    }

    /**
     * 给请求签名。
     *
     * @param params     请求参数
     * @param body       请求主体内容
     * @param secret     签名密钥
     * @param signMethod 签名方法
     * @return 签名 string
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     */
    public static String signRequest(Map<String, String> params, String body, String secret, String signMethod) throws IOException, GeneralSecurityException {
        // 1. 检查参数是否已经排序
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        // 2. 把所有参数名和参数值串在一起
        StringBuilder query = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key);
            if (StringUtils.areNotEmpty(key, value)) {
                query.append(key).append(value);
            }
        }

        // 3. 把请求主体拼接在参数后面
        if (body != null) {
            query.append(body);
        }

        // 4. 使用MD5/HMAC加密
        byte[] bytes;
        if (Constants.SIGN_METHOD_HMAC.equals(signMethod)) {
            bytes = encryptHMAC(query.toString(), secret);
        } else {
            query.append(secret);
            bytes = encryptMD5(query.toString());
        }

        // 5. 把二进制转化为大写的十六进制
        return byte2hex(bytes);
    }

    /**
     * 给请求签名。
     *
     * @param queryString 请求参数
     * @param body        请求主体内容
     * @param secret      签名密钥
     * @param signMethod  签名方法
     * @return 签名 string
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     */
    public static String signRequest(String queryString, String body, String secret, String signMethod) throws IOException, GeneralSecurityException {

        // 1. 判断参数是否存在
        StringBuilder query = new StringBuilder();
        if(queryString != null){
            query.append(queryString);
        }

        // 2. 把请求主体拼接在参数后面
        if (body != null) {
            query.append(body);
        }

        // 3. 使用MD5/HMAC加密
        byte[] bytes;
        if (Constants.SIGN_METHOD_HMAC.equals(signMethod)) {
            bytes = encryptHMAC(query.toString(), secret);
        } else {
            query.append(secret);
            bytes = encryptMD5(query.toString());
        }

        // 4. 把二进制转化为大写的十六进制
        return byte2hex(bytes);
    }

    private static byte[] encryptHMAC(String data, String secret) throws GeneralSecurityException, IOException {
        byte[] bytes;
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(Constants.CHARSET_UTF8), "HmacMD5");
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        bytes = mac.doFinal(data.getBytes(Constants.CHARSET_UTF8));
        return bytes;
    }

    /**
     * 对字符串采用UTF-8编码后，用MD5进行摘要。
     *
     * @param data the data
     * @return the byte [ ]
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     */
    public static byte[] encryptMD5(String data) throws IOException, GeneralSecurityException {
        return encryptMD5(data.getBytes(Constants.CHARSET_UTF8));
    }

    /**
     * 对字节流进行MD5摘要。
     *
     * @param data the data
     * @return the byte [ ]
     * @throws IOException              the io exception
     * @throws GeneralSecurityException the general security exception
     */
    public static byte[] encryptMD5(byte[] data) throws IOException, GeneralSecurityException {
        byte[] bytes;
        MessageDigest md = MessageDigest.getInstance("MD5");
        bytes = md.digest(data);
        return bytes;
    }

    /**
     * 把字节流转换为十六进制表示方式。
     *
     * @param bytes the bytes
     * @return the string
     */
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }


    /**
     * 使用AES加密原始字符串.
     *
     * @param input 加密内容
     * @param secret 加密密钥
     * @return base64 encoded string
     */
    public static String aesEncrypt(String input, String secret) {
        byte[] secretBytes = AlgHelper.hexStringToBytes(secret);
        byte[] secretKey = KeyUtils.genSecretKey(secretBytes);
        String result = null;
        try {
            result = bytesToHexString(aesEncrypt(input.getBytes(Constants.CHARSET_UTF8), secretKey));
        } catch (Exception e) {
            logger.error("AES encrypt ex", e);
        }
        return result;
    }

    /**
     * 使用AES加密原始字符串.
     *
     * @param input 原始输入字符数组
     * @param key   符合AES要求的密钥
     * @return the byte [ ]
     */
    public static byte[] aesEncrypt(byte[] input, byte[] key) {
        return aes(input, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * 使用AES加密原始字符串.
     *
     * @param input 原始输入字符数组
     * @param key   符合AES要求的密钥
     * @param iv    初始向量
     * @return the byte [ ]
     */
    public static byte[] aesEncrypt(byte[] input, byte[] key, byte[] iv) {
        return aes(input, key, iv, Cipher.ENCRYPT_MODE);
    }

    /**
     * 使用AES解密字符串, 返回原始字符串.
     *
     * @param input 解密内容
     * @param secret 解密密钥
     * @return base64 encoded string
     */
    public static String aesDecrypt(String input, String secret) {
        byte[] secretBytes = AlgHelper.hexStringToBytes(secret);
        byte[] secretKey = KeyUtils.genSecretKey(secretBytes);

        String result = null;
        try {
            result = new String(aesDecrypt(hexStringToBytes(input), secretKey), Constants.CHARSET_UTF8);
        } catch (Exception e) {
            logger.error("AES decrypt ex", e);
        }
        return result;
    }

    /**
     * 使用AES解密字符串, 返回原始字符串.
     *
     * @param input base64编码的加密字符串
     * @param key   符合AES要求的密钥
     * @return the byte [ ]
     */
    public static byte[] aesDecrypt(byte[] input, byte[] key) {
        return aes(input, key, Cipher.DECRYPT_MODE);
    }

    /**
     * 使用AES解密字符串, 返回原始字符串.
     *
     * @param input base64编码的加密字符串
     * @param key   符合AES要求的密钥
     * @param iv    初始向量
     * @return the byte [ ]
     */
    public static byte[] aesDecrypt(byte[] input, byte[] key, byte[] iv) {
        return aes(input, key, iv, Cipher.DECRYPT_MODE);
    }

    /**
     * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
     *
     * @param input 原始字节数组
     * @param key   符合AES要求的密钥
     * @param mode  Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
     */
    private static byte[] aes(byte[] input, byte[] key, int mode) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(mode, secretKey);
            return cipher.doFinal(input);
        } catch (GeneralSecurityException e) {
            logger.error("AES ex, mode:{}", mode, e);
            return null;
        }
    }

    /**
     * 使用AES加密或解密无编码的原始字节数组, 返回无编码的字节数组结果.
     *
     * @param input 原始字节数组
     * @param key   符合AES要求的密钥
     * @param iv    初始向量
     * @param mode  Cipher.ENCRYPT_MODE 或 Cipher.DECRYPT_MODE
     */
    private static byte[] aes(byte[] input, byte[] key, byte[] iv, int mode) {
        try {
            SecretKey secretKey = new SecretKeySpec(key, AES);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(AES_CBC);
            cipher.init(mode, secretKey, ivSpec);
            return cipher.doFinal(input);
        } catch (GeneralSecurityException e) {
            logger.error("AES encrypt ex", e);
            return null;
        }
    }


    /**
     * 生成AES密钥,可选长度为128,192,256位.
     *
     * @param keysize the keysize
     * @return the byte [ ]
     */
    public static byte[] generateAesKey(int keysize) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(keysize);
            SecretKey secretKey = keyGenerator.generateKey();
            return secretKey.getEncoded();
        } catch (GeneralSecurityException e) {
            logger.error("AES encrypt ex", e);
            return null;
        }
    }

    /**
     * 生成随机向量,默认大小为cipher.getBlockSize(), 16字节.
     *
     * @return the byte [ ]
     */
    public static byte[] generateIV() {
        byte[] bytes = new byte[DEFAULT_IV_SIZE];
        random.nextBytes(bytes);
        return bytes;
    }

    public static String bytesToHexString(byte[] paramArrayOfByte) {
        StringBuilder localStringBuilder = new StringBuilder();
        if ((paramArrayOfByte == null) || (paramArrayOfByte.length <= 0)) {
            return null;
        }
        for (int i = 0; i < paramArrayOfByte.length; i++) {
            int j = paramArrayOfByte[i] & 0xFF;
            String str = Integer.toHexString(j);
            if (str.length() < 2) {
                localStringBuilder.append(0);
            }
            localStringBuilder.append(str);
        }
        return localStringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String paramString) {
        paramString = paramString.toUpperCase();
        if ((paramString == null) || (paramString.equals(""))) {
            return null;
        }
        paramString = paramString.toUpperCase();
        int i = paramString.length() / 2;
        char[] arrayOfChar = paramString.toCharArray();
        byte[] arrayOfByte = new byte[i];
        for (int j = 0; j < i; j++) {
            int k = j * 2;
            arrayOfByte[j] = ((byte) (charToByte(arrayOfChar[k]) << 4 | (charToByte(arrayOfChar[(k + 1)]) & 0xff)));
        }
        return arrayOfByte;
    }

    public static byte charToByte(char paramChar) {
        return (byte) "0123456789ABCDEF".indexOf(paramChar);
    }
}
