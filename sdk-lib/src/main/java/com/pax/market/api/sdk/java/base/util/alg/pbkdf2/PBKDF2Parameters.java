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

public class PBKDF2Parameters {
    protected byte[] salt;
    protected int iterationCount;
    protected String hashAlgorithm;
    protected String hashCharset;
    protected byte[] derivedKey;

    public PBKDF2Parameters() {
        this.hashAlgorithm = null;
        this.hashCharset = "UTF-8";
        this.salt = null;
        this.iterationCount = 1000;
        this.derivedKey = null;
    }

    public PBKDF2Parameters(String paramString1, String paramString2, byte[] paramArrayOfByte, int paramInt) {
        this.hashAlgorithm = paramString1;
        this.hashCharset = paramString2;
        this.salt = paramArrayOfByte;
        this.iterationCount = paramInt;
        this.derivedKey = null;
    }

    public PBKDF2Parameters(String paramString1, String paramString2, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2) {
        this.hashAlgorithm = paramString1;
        this.hashCharset = paramString2;
        this.salt = paramArrayOfByte1;
        this.iterationCount = paramInt;
        this.derivedKey = paramArrayOfByte2;
    }

    public int getIterationCount() {
        return this.iterationCount;
    }

    public void setIterationCount(int paramInt) {
        this.iterationCount = paramInt;
    }

    public byte[] getSalt() {
        return this.salt;
    }

    public void setSalt(byte[] paramArrayOfByte) {
        this.salt = paramArrayOfByte;
    }

    public byte[] getDerivedKey() {
        return this.derivedKey;
    }

    public void setDerivedKey(byte[] paramArrayOfByte) {
        this.derivedKey = paramArrayOfByte;
    }

    public String getHashAlgorithm() {
        return this.hashAlgorithm;
    }

    public void setHashAlgorithm(String paramString) {
        this.hashAlgorithm = paramString;
    }

    public String getHashCharset() {
        return this.hashCharset;
    }

    public void setHashCharset(String paramString) {
        this.hashCharset = paramString;
    }
}