/*
 *
 *  * *******************************************************************************
 *  * COPYRIGHT
 *  *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *  *   This software is supplied under the terms of a license agreement or
 *  *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *  *   or disclosed except in accordance with the terms in that agreement.
 *  *
 *  *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 *  * *******************************************************************************
 *
 */

package com.pax.market.api.sdk.java.base.util;


import com.pax.market.api.sdk.java.base.util.alg.digest.SHA1;
import com.pax.market.api.sdk.java.base.util.alg.pbkdf2.PBKDF2;

public class KeyUtils {
    private static final int PBKDF2COUNTER = 10;
    private static final int SEEDLENGTH = 16;
    private static final String algType = "HMacSHA1";
    private static final byte[] shareKey = {109, 62, 34, 123, 104, 108, 33, 102, 124, 121, 21, 46, 17, 51, 85, 96, 100, 101, 44, 32, 116, 104, 105, 115, 32, 105, 115, 32, 116, 104, 101, 32, 50, 101, 51, 101, 105, 54, 101, 32, 59, 101, 121, 59, 32, 111, 110, 32, 116, 104, 101, 32, 115, 101, 114, 118, 101, 114, 32, 115, 105, 100, 119, 44, 32, 105, 116, 32, 105, 115, 32, 116, 104, 101, 32, 115, 101, 110, 106, 42, 44, 47, 108, 115, 35, 57, 115, 38, 118, 110, 108, 44, 114, 101, 99, 101, 105, 118, 101, 32, -14, -14, -14, -14, -14, -14, -14, -14, -14, -14, 91, 96, 108, 126, 32, 116, 104, 101, 32, 35, 43, 102, Byte.MAX_VALUE, 46, 108, 126, 110, 80};

    public static byte[] genSecretKey(byte[] bytes) {
        SHA1 sha1 = new SHA1();
        sha1.update(shareKey);
        byte[] digestBytes = sha1.digest();
        if (!StringUtils.byteNotNull(digestBytes)) {
            return null;
        }
        return PBKDF2.getKey(digestBytes, bytes, PBKDF2COUNTER, SEEDLENGTH, algType);
    }
}