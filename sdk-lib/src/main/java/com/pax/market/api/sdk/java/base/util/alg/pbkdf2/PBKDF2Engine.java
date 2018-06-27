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

import java.io.UnsupportedEncodingException;

public class PBKDF2Engine {
    protected PBKDF2Parameters parameters;
    protected PRF prf;

    public PBKDF2Engine() {
        this.parameters = null;
        this.prf = null;
    }

    public PBKDF2Engine(PBKDF2Parameters paramPBKDF2Parameters) {
        this.parameters = paramPBKDF2Parameters;
        this.prf = null;
    }

    public PBKDF2Engine(PBKDF2Parameters paramPBKDF2Parameters, PRF paramPRF) {
        this.parameters = paramPBKDF2Parameters;
        this.prf = paramPRF;
    }

    public byte[] deriveKey(String paramString) {
        return deriveKey(paramString, 0);
    }

    public byte[] deriveKey(String paramString, int paramInt) {
        byte[] arrayOfByte1 = null;
        byte[] arrayOfByte2 = null;
        String str = this.parameters.getHashCharset();
        if (paramString == null) {
            paramString = "";
        }
        try {
            if (str == null) {
                arrayOfByte2 = paramString.getBytes();
            } else {
                arrayOfByte2 = paramString.getBytes(str);
            }
        } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
            return null;
        }
        assertPRF(arrayOfByte2);
        if (paramInt == 0) {
            paramInt = this.prf.getHLen();
        }
        arrayOfByte1 = PBKDF2(this.prf, this.parameters.getSalt(), this.parameters.getIterationCount(), paramInt);
        return arrayOfByte1;
    }

    public byte[] deriveKey(byte[] paramArrayOfByte, int paramInt) {
        byte[] arrayOfByte = null;
        assertPRF(paramArrayOfByte);
        if (paramInt == 0) {
            paramInt = this.prf.getHLen();
        }
        arrayOfByte = PBKDF2(this.prf, this.parameters.getSalt(), this.parameters.getIterationCount(), paramInt);
        return arrayOfByte;
    }

    public boolean verifyKey(String paramString) {
        byte[] arrayOfByte1 = getParameters().getDerivedKey();
        if ((arrayOfByte1 == null) || (arrayOfByte1.length == 0)) {
            return false;
        }
        byte[] arrayOfByte2 = deriveKey(paramString, arrayOfByte1.length);
        if ((arrayOfByte2 == null) || (arrayOfByte2.length != arrayOfByte1.length)) {
            return false;
        }
        for (int i = 0; i < arrayOfByte2.length; i++) {
            if (arrayOfByte2[i] != arrayOfByte1[i]) {
                return false;
            }
        }
        return true;
    }

    protected void assertPRF(byte[] paramArrayOfByte) {
        if (this.prf == null) {
            this.prf = new HMacBasedPRF(this.parameters.getHashAlgorithm());
        }
        this.prf.init(paramArrayOfByte);
    }

    public PRF getPseudoRandomFunction() {
        return this.prf;
    }

    protected byte[] PBKDF2(PRF paramPRF, byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        if (paramArrayOfByte == null) {
            paramArrayOfByte = new byte[0];
        }
        int i = paramPRF.getHLen();
        int j = ceil(paramInt2, i);
        int k = paramInt2 - (j - 1) * i;
        byte[] arrayOfByte1 = new byte[j * i];
        int m = 0;
        for (int n = 1; n <= j; n++) {
            _F(arrayOfByte1, m, paramPRF, paramArrayOfByte, paramInt1, n);
            m += i;
        }
        if (k < i) {
            byte[] arrayOfByte2 = new byte[paramInt2];
            System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, paramInt2);
            return arrayOfByte2;
        }
        return arrayOfByte1;
    }

    protected int ceil(int paramInt1, int paramInt2) {
        int i = 0;
        if (paramInt1 % paramInt2 > 0) {
            i = 1;
        }
        return paramInt1 / paramInt2 + i;
    }

    protected void _F(byte[] paramArrayOfByte1, int paramInt1, PRF paramPRF, byte[] paramArrayOfByte2, int paramInt2, int paramInt3) {
        int i = paramPRF.getHLen();
        byte[] arrayOfByte1 = new byte[i];
        byte[] arrayOfByte2 = new byte[paramArrayOfByte2.length + 4];
        System.arraycopy(paramArrayOfByte2, 0, arrayOfByte2, 0, paramArrayOfByte2.length);
        INT(arrayOfByte2, paramArrayOfByte2.length, paramInt3);
        for (int j = 0; j < paramInt2; j++) {
            arrayOfByte2 = paramPRF.doFinal(arrayOfByte2);
            xor(arrayOfByte1, arrayOfByte2);
        }
        System.arraycopy(arrayOfByte1, 0, paramArrayOfByte1, paramInt1, i);
    }

    protected void xor(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) {
        for (int i = 0; i < paramArrayOfByte1.length; i++) {
            int tmp10_9 = i;
            byte[] tmp10_8 = paramArrayOfByte1;
            tmp10_8[tmp10_9] = ((byte) (tmp10_8[tmp10_9] ^ paramArrayOfByte2[i]));
        }
    }

    protected void INT(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        paramArrayOfByte[(paramInt1 + 0)] = ((byte) (paramInt2 / 16777216));
        paramArrayOfByte[(paramInt1 + 1)] = ((byte) (paramInt2 / 65536));
        paramArrayOfByte[(paramInt1 + 2)] = ((byte) (paramInt2 / 256));
        paramArrayOfByte[(paramInt1 + 3)] = ((byte) paramInt2);
    }

    public PBKDF2Parameters getParameters() {
        return this.parameters;
    }

    public void setParameters(PBKDF2Parameters paramPBKDF2Parameters) {
        this.parameters = paramPBKDF2Parameters;
    }

    public void setPseudoRandomFunction(PRF paramPRF) {
        this.prf = paramPRF;
    }
}