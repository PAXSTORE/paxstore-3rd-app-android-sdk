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

package com.pax.market.api.sdk.java.base.util.alg.hmac;


import com.pax.market.api.sdk.java.base.util.alg.digest.IDigest;

public class HMac {
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    private IDigest digest;
    private int digestLength;
    private int blockLength;
    private byte[] inputPad;
    private byte[] outputPad;

    public HMac(IDigest paramIDigest) {
        this.digest = paramIDigest;
        this.digestLength = paramIDigest.getDigestLength();
        this.blockLength = paramIDigest.getBlockLength();
        this.inputPad = new byte[this.blockLength];
        this.outputPad = new byte[this.blockLength];
    }

    public String getAlgorithmName() {
        return "HMAC-" + this.digest.getAlgorithmName();
    }

    public void init(byte[] paramArrayOfByte) {
        this.digest.reset();
        if (paramArrayOfByte.length > this.blockLength) {
            this.digest.update(paramArrayOfByte, 0, paramArrayOfByte.length);
            this.digest.digest(this.inputPad);
            for (int i = this.digestLength; i < this.inputPad.length; i++) {
                this.inputPad[i] = 0;
            }
        } else {
            System.arraycopy(paramArrayOfByte, 0, this.inputPad, 0, paramArrayOfByte.length);
            for (int i = paramArrayOfByte.length; i < this.inputPad.length; i++) {
                this.inputPad[i] = 0;
            }
        }
        this.outputPad = new byte[this.inputPad.length];
        System.arraycopy(this.inputPad, 0, this.outputPad, 0, this.inputPad.length);
        for (int i = 0; i < this.inputPad.length; i++) {
            int tmp156_155 = i;
            byte[] tmp156_152 = this.inputPad;
            tmp156_152[tmp156_155] = ((byte) (tmp156_152[tmp156_155] ^ 0x36));
        }
        for (int i = 0; i < this.outputPad.length; i++) {
            int tmp185_184 = i;
            byte[] tmp185_181 = this.outputPad;
            tmp185_181[tmp185_184] = ((byte) (tmp185_181[tmp185_184] ^ 0x5C));
        }
        this.digest.update(this.inputPad, 0, this.inputPad.length);
    }

    public int getMacLength() {
        return this.digestLength;
    }

    public void update(byte paramByte) {
        this.digest.update(paramByte);
    }

    public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        this.digest.update(paramArrayOfByte, paramInt1, paramInt2);
    }

    public void update(byte[] paramArrayOfByte) {
        this.digest.update(paramArrayOfByte);
    }

    public int doFinal(byte[] paramArrayOfByte, int paramInt) {
        byte[] arrayOfByte = new byte[this.digestLength];
        this.digest.digest(arrayOfByte, 0);
        this.digest.update(this.outputPad, 0, this.outputPad.length);
        this.digest.update(arrayOfByte, 0, arrayOfByte.length);
        this.digest.digest(paramArrayOfByte, paramInt);
        reset();
        return this.digestLength;
    }

    public byte[] doFinal() {
        byte[] arrayOfByte = new byte[this.digestLength];
        doFinal(arrayOfByte, 0);
        return arrayOfByte;
    }

    public void reset() {
        this.digest.reset();
        this.digest.update(this.inputPad, 0, this.inputPad.length);
    }
}