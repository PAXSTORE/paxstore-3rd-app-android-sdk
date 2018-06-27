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

public abstract interface IDigest {
    public abstract String getAlgorithmName();

    public abstract int getDigestLength();

    public abstract int getBlockLength();

    public abstract void update(byte paramByte);

    public abstract void update(byte[] paramArrayOfByte);

    public abstract void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

    public abstract void digest(byte[] paramArrayOfByte, int paramInt);

    public abstract void digest(byte[] paramArrayOfByte);

    public abstract byte[] digest();

    public abstract void reset();
}