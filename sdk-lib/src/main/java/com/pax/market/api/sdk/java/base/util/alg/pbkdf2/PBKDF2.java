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

package com.pax.market.api.sdk.java.base.util.alg.pbkdf2;

public class PBKDF2 {
    public static byte[] getKey(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt1, int paramInt2, String paramString) {
        PBKDF2Parameters localPBKDF2Parameters = new PBKDF2Parameters(paramString, null, paramArrayOfByte2, paramInt1);
        PBKDF2Engine localPBKDF2Engine = new PBKDF2Engine(localPBKDF2Parameters);
        return localPBKDF2Engine.deriveKey(paramArrayOfByte1, paramInt2);
    }
}