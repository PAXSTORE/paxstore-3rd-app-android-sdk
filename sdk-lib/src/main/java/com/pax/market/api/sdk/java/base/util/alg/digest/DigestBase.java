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

package com.pax.market.api.sdk.java.base.util.alg.digest;

public abstract class DigestBase
        implements IDigest {
    protected String algName;
    protected int digestLen;
    protected int blockLen;
    protected byte[] xBuf = new byte[4];
    protected int xBufOff = 0;
    protected long byteCount;

    public String getAlgorithmName() {
        return this.algName;
    }

    public int getBlockLength() {
        return this.blockLen;
    }

    public int getDigestLength() {
        return this.digestLen;
    }

    public void update(byte paramByte) {
        this.xBuf[(this.xBufOff++)] = paramByte;
        if (this.xBufOff == this.xBuf.length) {
            processWord(this.xBuf, 0);
            this.xBufOff = 0;
        }
        this.byteCount += 1L;
    }

    public void update(byte[] paramArrayOfByte) {
        update(paramArrayOfByte, 0, paramArrayOfByte.length);
    }

    public void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        while ((this.xBufOff != 0) && (paramInt2 > 0)) {
            update(paramArrayOfByte[paramInt1]);
            paramInt1++;
            paramInt2--;
        }
        while (paramInt2 > this.xBuf.length) {
            processWord(paramArrayOfByte, paramInt1);
            paramInt1 += this.xBuf.length;
            paramInt2 -= this.xBuf.length;
            this.byteCount += this.xBuf.length;
        }
        while (paramInt2 > 0) {
            update(paramArrayOfByte[paramInt1]);
            paramInt1++;
            paramInt2--;
        }
    }

    public void digest(byte[] paramArrayOfByte) {
        digest(paramArrayOfByte, 0);
    }

    public byte[] digest() {
        byte[] arrayOfByte = new byte[this.digestLen];
        digest(arrayOfByte);
        return arrayOfByte;
    }

    protected void finish() {
        long l = this.byteCount << 3;
        update((byte) Byte.MIN_VALUE);
        while (this.xBufOff != 0) {
            update((byte) 0);
        }
        processLength(l);
        processBlock();
    }

    public void reset() {
        this.byteCount = 0L;
        this.xBufOff = 0;
        for (int i = 0; i < this.xBuf.length; i++) {
            this.xBuf[i] = 0;
        }
    }

    protected abstract void processWord(byte[] paramArrayOfByte, int paramInt);

    protected abstract void processLength(long paramLong);

    protected abstract void processBlock();
}