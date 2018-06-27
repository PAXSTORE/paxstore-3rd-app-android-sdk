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

package com.pax.market.api.sdk.java.base.util.alg.gm;


import com.pax.market.api.sdk.java.base.util.AlgHelper;
import com.pax.market.api.sdk.java.base.util.alg.digest.DigestBase;
import com.pax.market.api.sdk.java.base.util.alg.digest.IDigest;

public class SM3 extends DigestBase implements IDigest {
    private int[] X = new int[68];
    private int[] Y = new int[64];
    private int xOff;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private int H6;
    private int H7;
    private int H8;
    private static final int T1 = 2043430169;
    private static final int T2 = 2055708042;

    public SM3() {
        this.algName = "SM3";
        this.digestLen = 32;
        this.blockLen = 64;
        reset();
    }

    protected void processBlock() {
        for (int i = 16; i < 68; i++) {
            this.X[i] = (P1(this.X[(i - 16)] ^ this.X[(i - 9)] ^ (this.X[(i - 3)] << 15 | this.X[(i - 3)] >>> 17)) ^ (this.X[(i - 13)] << 7 | this.X[(i - 13)] >>> 25) ^ this.X[(i - 6)]);
        }
        for (int i = 0; i < 64; i++) {
            this.Y[i] = (this.X[i] ^ this.X[(i + 4)]);
        }
        int i = this.H1;
        int j = this.H2;
        int k = this.H3;
        int m = this.H4;
        int n = this.H5;
        int i1 = this.H6;
        int i2 = this.H7;
        int i3 = this.H8;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        for (int i9 = 0; i9 < 16; i9++) {
            i4 = (i << 12 | i >>> 20) + n + (2043430169 << i9 | 2043430169 >>> 32 - i9);
            i5 = i4 << 7 | i4 >>> 25;
            i6 = i5 ^ (i << 12 | i >>> 20);
            i7 = f1(i, j, k) + m + i6 + this.Y[i9];
            i8 = g1(n, i1, i2) + i3 + i5 + this.X[i9];
            m = k;
            k = j << 9 | j >>> 23;
            j = i;
            i = i7;
            i3 = i2;
            i2 = i1 << 19 | i1 >>> 13;
            i1 = n;
            n = P0(i8);
        }
        for (int i9 = 16; i9 < 64; i9++) {
            i4 = (i << 12 | i >>> 20) + n + (2055708042 << i9 | 2055708042 >>> 32 - i9);
            i5 = i4 << 7 | i4 >>> 25;
            i6 = i5 ^ (i << 12 | i >>> 20);
            i7 = f2(i, j, k) + m + i6 + this.Y[i9];
            i8 = g2(n, i1, i2) + i3 + i5 + this.X[i9];
            m = k;
            k = j << 9 | j >>> 23;
            j = i;
            i = i7;
            i3 = i2;
            i2 = i1 << 19 | i1 >>> 13;
            i1 = n;
            n = P0(i8);
        }
        this.H1 ^= i;
        this.H2 ^= j;
        this.H3 ^= k;
        this.H4 ^= m;
        this.H5 ^= n;
        this.H6 ^= i1;
        this.H7 ^= i2;
        this.H8 ^= i3;
        this.xOff = 0;
        for (int i9 = 0; i9 < 16; i9++) {
            this.X[i9] = 0;
        }
    }

    protected void processLength(long paramLong) {
        if (this.xOff > 14) {
            processBlock();
        }
        this.X[14] = ((int) (paramLong >>> 32));
        this.X[15] = ((int) (paramLong & -1L));
    }

    protected void processWord(byte[] paramArrayOfByte, int paramInt) {
        int i = paramArrayOfByte[paramInt] << 24;
        i |= (paramArrayOfByte[(++paramInt)] & 0xFF) << 16;
        i |= (paramArrayOfByte[(++paramInt)] & 0xFF) << 8;
        i |= paramArrayOfByte[(++paramInt)] & 0xFF;
        this.X[this.xOff] = i;
        if (++this.xOff == 16) {
            processBlock();
        }
    }

    private int P0(int paramInt) {
        return paramInt ^ (paramInt << 9 | paramInt >>> 23) ^ (paramInt << 17 | paramInt >>> 15);
    }

    private int P1(int paramInt) {
        return paramInt ^ (paramInt << 15 | paramInt >>> 17) ^ (paramInt << 23 | paramInt >>> 9);
    }

    private int f1(int paramInt1, int paramInt2, int paramInt3) {
        return paramInt1 ^ paramInt2 ^ paramInt3;
    }

    private int f2(int paramInt1, int paramInt2, int paramInt3) {
        return paramInt1 & paramInt2 | paramInt1 & paramInt3 | paramInt2 & paramInt3;
    }

    private int g1(int paramInt1, int paramInt2, int paramInt3) {
        return paramInt1 ^ paramInt2 ^ paramInt3;
    }

    private int g2(int paramInt1, int paramInt2, int paramInt3) {
        return paramInt1 & paramInt2 | ~paramInt1 & paramInt3;
    }

    public void reset() {
        super.reset();
        this.H1 = 1937774191;
        this.H2 = 1226093241;
        this.H3 = 388252375;
        this.H4 = -628488704;
        this.H5 = -1452330820;
        this.H6 = 372324522;
        this.H7 = -477237683;
        this.H8 = -1325724082;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
        for (int i = 0; i != this.Y.length; i++) {
            this.Y[i] = 0;
        }
    }

    public void digest(byte[] paramArrayOfByte, int paramInt) {
        finish();
        AlgHelper.intToBigEndian(this.H1, paramArrayOfByte, paramInt);
        AlgHelper.intToBigEndian(this.H2, paramArrayOfByte, paramInt + 4);
        AlgHelper.intToBigEndian(this.H3, paramArrayOfByte, paramInt + 8);
        AlgHelper.intToBigEndian(this.H4, paramArrayOfByte, paramInt + 12);
        AlgHelper.intToBigEndian(this.H5, paramArrayOfByte, paramInt + 16);
        AlgHelper.intToBigEndian(this.H6, paramArrayOfByte, paramInt + 20);
        AlgHelper.intToBigEndian(this.H7, paramArrayOfByte, paramInt + 24);
        AlgHelper.intToBigEndian(this.H8, paramArrayOfByte, paramInt + 28);
        reset();
    }
}