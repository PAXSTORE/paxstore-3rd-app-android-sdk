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


import com.pax.market.api.sdk.java.base.util.alg.digest.SHA1;
import com.pax.market.api.sdk.java.base.util.alg.gm.SM3;
import com.pax.market.api.sdk.java.base.util.alg.hmac.HMac;

public class HMacBasedPRF
        implements PRF {
    protected HMac hMac;
    protected int hLen;
    protected String macAlgorithm;

    public HMacBasedPRF(String paramString) {
        this.macAlgorithm = paramString;
        if (paramString.equals("HMacSHA1")) {
            this.hMac = new HMac(new SHA1());
        } else {
            this.hMac = new HMac(new SM3());
        }
        this.hLen = this.hMac.getMacLength();
    }

    public byte[] doFinal(byte[] paramArrayOfByte) {
        this.hMac.update(paramArrayOfByte, 0, paramArrayOfByte.length);
        return this.hMac.doFinal();
    }

    public int getHLen() {
        return this.hLen;
    }

    public void init(byte[] paramArrayOfByte) {
        this.hMac.init(paramArrayOfByte);
    }
}