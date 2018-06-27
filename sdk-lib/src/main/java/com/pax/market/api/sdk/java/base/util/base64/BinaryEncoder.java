/*
 * *******************************************************************************
 * COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *
 *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 * *******************************************************************************
 */

package com.pax.market.api.sdk.java.base.util.base64;

/**
 * Defines common encoding methods for byte array encoders.
 *
 * @author Apache Software Foundation
 * @version $Id : BinaryEncoder.java,v 1.10 2004/02/29 04:08:31 tobrien Exp $
 */
public interface BinaryEncoder extends Encoder {

    /**
     * Encodes a byte array and return the encoded data
     * as a byte array.
     *
     * @param pArray Data to be encoded
     * @return A byte array containing the encoded data
     * @throws EncoderException thrown if the Encoder                          encounters a failure condition during the                          encoding process.
     */
    byte[] encode(byte[] pArray) throws EncoderException;
}  

