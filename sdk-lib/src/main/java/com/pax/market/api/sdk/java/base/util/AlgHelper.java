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

/**
 * The type Alg helper.
 */
public class AlgHelper {
    /**
     * The Hex 2 bin table.
     */
    static byte[] hex2bin_table = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 10, 11, 12, 13, 14, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 11, 12, 13, 14, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    /**
     * Big endian to short short.
     *
     * @param paramArrayOfByte the param array of byte
     * @param paramInt         the param int
     * @return the short
     */
    public static short bigEndianToShort(byte[] paramArrayOfByte, int paramInt) {
        int i = paramArrayOfByte[paramInt] << 8;
        i |= paramArrayOfByte[(++paramInt)] & 0xFF;
        return (short) i;
    }

    /**
     * Short to big endian.
     *
     * @param paramShort       the param short
     * @param paramArrayOfByte the param array of byte
     * @param paramInt         the param int
     */
    public static void shortToBigEndian(short paramShort, byte[] paramArrayOfByte, int paramInt) {
        paramArrayOfByte[paramInt] = ((byte) (paramShort >>> 8));
        paramArrayOfByte[(++paramInt)] = ((byte) paramShort);
    }

    /**
     * Big endian to int int.
     *
     * @param paramArrayOfByte the param array of byte
     * @param paramInt         the param int
     * @return the int
     */
    public static int bigEndianToInt(byte[] paramArrayOfByte, int paramInt) {
        int i = paramArrayOfByte[paramInt] << 24;
        i |= (paramArrayOfByte[(++paramInt)] & 0xFF) << 16;
        i |= (paramArrayOfByte[(++paramInt)] & 0xFF) << 8;
        i |= paramArrayOfByte[(++paramInt)] & 0xFF;
        return i;
    }

    /**
     * Int to big endian.
     *
     * @param paramInt1        the param int 1
     * @param paramArrayOfByte the param array of byte
     * @param paramInt2        the param int 2
     */
    public static void intToBigEndian(int paramInt1, byte[] paramArrayOfByte, int paramInt2) {
        paramArrayOfByte[paramInt2] = ((byte) (paramInt1 >>> 24));
        paramArrayOfByte[(++paramInt2)] = ((byte) (paramInt1 >>> 16));
        paramArrayOfByte[(++paramInt2)] = ((byte) (paramInt1 >>> 8));
        paramArrayOfByte[(++paramInt2)] = ((byte) paramInt1);
    }

    /**
     * Big endian to long long.
     *
     * @param paramArrayOfByte the param array of byte
     * @param paramInt         the param int
     * @return the long
     */
    public static long bigEndianToLong(byte[] paramArrayOfByte, int paramInt) {
        int i = bigEndianToInt(paramArrayOfByte, paramInt);
        int j = bigEndianToInt(paramArrayOfByte, paramInt + 4);
        return (i & 0xFFFFFFFF) << 32 | j & 0xFFFFFFFF;
    }

    /**
     * Long to big endian.
     *
     * @param paramLong        the param long
     * @param paramArrayOfByte the param array of byte
     * @param paramInt         the param int
     */
    public static void longToBigEndian(long paramLong, byte[] paramArrayOfByte, int paramInt) {
        intToBigEndian((int) (paramLong >>> 32), paramArrayOfByte, paramInt);
        intToBigEndian((int) (paramLong & 0xFFFFFFFF), paramArrayOfByte, paramInt + 4);
    }

    /**
     * Hex string to bytes byte [ ].
     *
     * @param paramString the param string
     * @return the byte [ ]
     */
    public static byte[] hexStringToBytes(String paramString) {
        if ((paramString == null) || (paramString.length() % 2 != 0)) {
            return null;
        }
        int i = paramString.length() / 2;
        byte[] arrayOfByte = new byte[i];
        for (int j = 0; j < i; j++) {
            arrayOfByte[j] = ((byte) (hex2bin_table[paramString.charAt(j * 2)] << 4 | (hex2bin_table[paramString.charAt(j * 2 + 1)] & 0xff )));
        }
        return arrayOfByte;
    }

    /**
     * Byte to hex string.
     *
     * @param paramByte the param byte
     * @return the string
     */
    public static String byteToHex(byte paramByte) {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("0123456789ABCDEF".charAt(0xF & paramByte >> 4));
        localStringBuilder.append("0123456789ABCDEF".charAt(paramByte & 0xF));
        return localStringBuilder.toString();
    }

    /**
     * Bytes to hexs string.
     *
     * @param paramArrayOfByte the param array of byte
     * @return the string
     */
    public static String bytesToHexs(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return "";
        }
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i = 0; i < paramArrayOfByte.length; i++) {
            localStringBuilder.append(byteToHex(paramArrayOfByte[i]));
        }
        return localStringBuilder.toString();
    }

    /**
     * The entry point of application.
     *
     * @param paramArrayOfString the input arguments
     */
    public static void main(String[] paramArrayOfString) {
        String str = "138105898351234567890UDLJDJLDKJKDLJDYHFKF";
        System.out.println(str);
    }

    /**
     * Array to string string.
     *
     * @param paramArrayOfByte the param array of byte
     * @return the string
     */
    public static final String arrayToString(byte[] paramArrayOfByte) {
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i = 0; i < paramArrayOfByte.length; i++) {
            localStringBuilder.append(paramArrayOfByte[i] + " ");
        }
        return localStringBuilder.toString();
    }

    /**
     * Int 2 byte array byte [ ].
     *
     * @param paramInt the param int
     * @return the byte [ ]
     */
    public static byte[] int2byteArray(int paramInt) {
        byte[] arrayOfByte = new byte[4];
        arrayOfByte[0] = ((byte) (paramInt >>> 24));
        arrayOfByte[1] = ((byte) (paramInt >>> 16));
        arrayOfByte[2] = ((byte) (paramInt >>> 8));
        arrayOfByte[3] = ((byte) paramInt);
        return arrayOfByte;
    }

    /**
     * Byte array 2 int int.
     *
     * @param paramArrayOfByte the param array of byte
     * @return the int
     */
    public static int byteArray2int(byte[] paramArrayOfByte) {
        byte[] arrayOfByte = new byte[4];
        int i = arrayOfByte.length - 1;
        for (int j = paramArrayOfByte.length - 1; i >= 0; j--) {
            if (j >= 0) {
                arrayOfByte[i] = paramArrayOfByte[j];
            } else {
                arrayOfByte[i] = 0;
            }
            i--;
        }
        int k = (arrayOfByte[0] & 0xFF) << 24;
        int m = (arrayOfByte[1] & 0xFF) << 16;
        int n = (arrayOfByte[2] & 0xFF) << 8;
        int i1 = arrayOfByte[3] & 0xFF;
        return k + m + n + i1;
    }

    /**
     * Bytes 2 hex string string.
     *
     * @param paramArrayOfByte the param array of byte
     * @return the string
     */
    public static String bytes2HexString(byte[] paramArrayOfByte) {
        String str1 = "";
        for (int i = 0; i < paramArrayOfByte.length; i++) {
            String str2 = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
            if (str2.length() == 1) {
                str2 = '0' + str2;
            }
            str1 = str1 + str2.toUpperCase();
        }
        return str1;
    }

    /**
     * Bytes 2 hex string ex string.
     *
     * @param paramArrayOfByte the param array of byte
     * @param paramInt         the param int
     * @return the string
     */
    public static String bytes2HexStringEx(byte[] paramArrayOfByte, int paramInt) {
        String str1 = "";
        for (int i = 0; i < paramInt; i++) {
            String str2 = Integer.toHexString(paramArrayOfByte[i] & 0xFF);
            if (str2.length() == 1) {
                str2 = '0' + str2;
            }
            str1 = str1 + str2.toUpperCase();
        }
        return str1;
    }

    /**
     * Hex to string string.
     *
     * @param paramString the param string
     * @return the string
     */
    public static String hexToString(String paramString) {
        if (paramString == null) {
            return "";
        }
        int i = paramString.length() & 0xFFFFFFFE;
        StringBuilder localStringBuilder = new StringBuilder(i / 2);
        hexToString(paramString, localStringBuilder);
        return localStringBuilder.toString();
    }

    /**
     * Hex to string.
     *
     * @param paramString        the param string
     * @param paramStringBuilder the param string builder
     */
    public static void hexToString(String paramString, StringBuilder paramStringBuilder) {
        if (paramString == null) {
            return;
        }
        int i = paramString.length() & 0xFFFFFFFE;
        for (int j = 0; j < i; j += 2) {
            int k = 0;
            try {
                k = Integer.parseInt(paramString.substring(j, j + 2), 16);
            } catch (NumberFormatException localNumberFormatException) {
            }
            paramStringBuilder.append((char) k);
        }
    }

    /**
     * String to hex string.
     *
     * @param paramString the param string
     * @return the string
     */
    public static final String stringToHex(String paramString) {
        if (StringUtils.isEmpty(paramString)) {
            return "";
        }
        int i = paramString.length();
        StringBuilder localStringBuilder = new StringBuilder(i * 2);
        stringToHex(paramString, localStringBuilder);
        return localStringBuilder.toString().toUpperCase();
    }

    /**
     * String to hex.
     *
     * @param paramString        the param string
     * @param paramStringBuilder the param string builder
     */
    public static final void stringToHex(String paramString, StringBuilder paramStringBuilder) {
        if (paramString == null) {
            return;
        }
        int i = paramString.length();
        for (int j = 0; j < i; j++) {
            int k = paramString.charAt(j);
            for (int m = 0; m < 2; m++) {
                int n = k & 0xF0;
                k <<= 4;
                n >>= 4;
                if (n >= 10) {
                    paramStringBuilder.append((char) (n + 87));
                } else {
                    paramStringBuilder.append((char) (n + 48));
                }
            }
        }
    }

    /**
     * Bytes to int int.
     *
     * @param paramArrayOfByte the param array of byte
     * @return the int
     */
    public static int bytesToInt(byte[] paramArrayOfByte) {
        int i = 0;
        for (int j = 0; j < paramArrayOfByte.length; j++) {
            i += ((paramArrayOfByte[j] & 0xFF) << 8 * (3 - j));
        }
        return i;
    }

    /**
     * Int to byte byte [ ].
     *
     * @param paramInt the param int
     * @return the byte [ ]
     */
    public static byte[] intToByte(int paramInt) {
        byte[] arrayOfByte = new byte[4];
        for (int i = 0; i < 4; i++) {
            arrayOfByte[i] = ((byte) (paramInt >> 24));
            paramInt <<= 8;
        }
        return arrayOfByte;
    }

    /**
     * Bytes compare boolean.
     *
     * @param paramArrayOfByte1 the param array of byte 1
     * @param paramArrayOfByte2 the param array of byte 2
     * @return the boolean
     */
    public static boolean bytesCompare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
        if (paramArrayOfByte1.length != paramArrayOfByte2.length) {
            return false;
        }
        for (int i = 0; i < paramArrayOfByte1.length; i++) {
            if (paramArrayOfByte1[i] != paramArrayOfByte2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Org byte array.
     *
     * @param paramArrayOfByte1 the param array of byte 1
     * @param paramInt          the param int
     * @param paramArrayOfByte2 the param array of byte 2
     */
    public static void orgByteArray(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2) {
        for (int i = 0; i < paramArrayOfByte2.length; i++) {
            paramArrayOfByte1[paramInt] = paramArrayOfByte2[i];
            paramInt += 1;
        }
    }
}