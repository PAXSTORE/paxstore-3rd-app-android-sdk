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

import com.pax.market.api.sdk.java.base.util.AlgHelper;

public class SHA1 extends DigestBase {
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private int[] X = new int[80];
    private int xOff;
    private static final int Y1 = 1518500249;
    private static final int Y2 = 1859775393;
    private static final int Y3 = -1894007588;
    private static final int Y4 = -899497514;

    public SHA1() {
        this.algName = "SHA1";
        this.digestLen = 20;
        this.blockLen = 64;
        reset();
    }

    public void digest(byte[] paramArrayOfByte, int paramInt) {
        finish();
        AlgHelper.intToBigEndian(this.H1, paramArrayOfByte, paramInt);
        AlgHelper.intToBigEndian(this.H2, paramArrayOfByte, paramInt + 4);
        AlgHelper.intToBigEndian(this.H3, paramArrayOfByte, paramInt + 8);
        AlgHelper.intToBigEndian(this.H4, paramArrayOfByte, paramInt + 12);
        AlgHelper.intToBigEndian(this.H5, paramArrayOfByte, paramInt + 16);
        reset();
    }

    public void reset() {
        super.reset();
        this.H1 = 1732584193;
        this.H2 = -271733879;
        this.H3 = -1732584194;
        this.H4 = 271733878;
        this.H5 = -1009589776;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }

    protected void processWord(byte[] paramArrayOfByte, int paramInt) {
        this.X[this.xOff] = AlgHelper.bigEndianToInt(paramArrayOfByte, paramInt);
        if (++this.xOff == 16) {
            processBlock();
        }
    }

    protected void processLength(long paramLong) {
        if (this.xOff > 14) {
            processBlock();
        }
        this.X[14] = ((int) (paramLong >>> 32));
        this.X[15] = ((int) (paramLong & -1L));
    }

    private int f(int paramInt1, int paramInt2, int paramInt3) {
        return paramInt1 & paramInt2 | (paramInt1 ^ 0xFFFFFFFF) & paramInt3;
    }

    private int h(int paramInt1, int paramInt2, int paramInt3) {
        return paramInt1 ^ paramInt2 ^ paramInt3;
    }

    private int g(int paramInt1, int paramInt2, int paramInt3) {
        return paramInt1 & paramInt2 | paramInt1 & paramInt3 | paramInt2 & paramInt3;
    }

    protected void processBlock() {
        for (int i = 16; i < 80; i++) {
            int j = this.X[(i - 3)] ^ this.X[(i - 8)] ^ this.X[(i - 14)] ^ this.X[(i - 16)];
            this.X[i] = (j << 1 | j >>> 31);
        }
        int i = this.H1;
        int j = this.H2;
        int k = this.H3;
        int m = this.H4;
        int n = this.H5;
        int i1 = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            n += (i << 5 | i >>> 27) + f(j, k, m) + this.X[(i1++)] + 1518500249;
            j = j << 30 | j >>> 2;
            m += (n << 5 | n >>> 27) + f(i, j, k) + this.X[(i1++)] + 1518500249;
            i = i << 30 | i >>> 2;
            k += (m << 5 | m >>> 27) + f(n, i, j) + this.X[(i1++)] + 1518500249;
            n = n << 30 | n >>> 2;
            j += (k << 5 | k >>> 27) + f(m, n, i) + this.X[(i1++)] + 1518500249;
            m = m << 30 | m >>> 2;
            i += (j << 5 | j >>> 27) + f(k, m, n) + this.X[(i1++)] + 1518500249;
            k = k << 30 | k >>> 2;
        }
        for (int i2 = 0; i2 < 4; i2++) {
            n += (i << 5 | i >>> 27) + h(j, k, m) + this.X[(i1++)] + 1859775393;
            j = j << 30 | j >>> 2;
            m += (n << 5 | n >>> 27) + h(i, j, k) + this.X[(i1++)] + 1859775393;
            i = i << 30 | i >>> 2;
            k += (m << 5 | m >>> 27) + h(n, i, j) + this.X[(i1++)] + 1859775393;
            n = n << 30 | n >>> 2;
            j += (k << 5 | k >>> 27) + h(m, n, i) + this.X[(i1++)] + 1859775393;
            m = m << 30 | m >>> 2;
            i += (j << 5 | j >>> 27) + h(k, m, n) + this.X[(i1++)] + 1859775393;
            k = k << 30 | k >>> 2;
        }
        for (int i2 = 0; i2 < 4; i2++) {
            n += (i << 5 | i >>> 27) + g(j, k, m) + this.X[(i1++)] + -1894007588;
            j = j << 30 | j >>> 2;
            m += (n << 5 | n >>> 27) + g(i, j, k) + this.X[(i1++)] + -1894007588;
            i = i << 30 | i >>> 2;
            k += (m << 5 | m >>> 27) + g(n, i, j) + this.X[(i1++)] + -1894007588;
            n = n << 30 | n >>> 2;
            j += (k << 5 | k >>> 27) + g(m, n, i) + this.X[(i1++)] + -1894007588;
            m = m << 30 | m >>> 2;
            i += (j << 5 | j >>> 27) + g(k, m, n) + this.X[(i1++)] + -1894007588;
            k = k << 30 | k >>> 2;
        }
        for (int i2 = 0; i2 <= 3; i2++) {
            n += (i << 5 | i >>> 27) + h(j, k, m) + this.X[(i1++)] + -899497514;
            j = j << 30 | j >>> 2;
            m += (n << 5 | n >>> 27) + h(i, j, k) + this.X[(i1++)] + -899497514;
            i = i << 30 | i >>> 2;
            k += (m << 5 | m >>> 27) + h(n, i, j) + this.X[(i1++)] + -899497514;
            n = n << 30 | n >>> 2;
            j += (k << 5 | k >>> 27) + h(m, n, i) + this.X[(i1++)] + -899497514;
            m = m << 30 | m >>> 2;
            i += (j << 5 | j >>> 27) + h(k, m, n) + this.X[(i1++)] + -899497514;
            k = k << 30 | k >>> 2;
        }
        this.H1 += i;
        this.H2 += j;
        this.H3 += k;
        this.H4 += m;
        this.H5 += n;
        this.xOff = 0;
        for (int i2 = 0; i2 < 16; i2++) {
            this.X[i2] = 0;
        }
    }
}