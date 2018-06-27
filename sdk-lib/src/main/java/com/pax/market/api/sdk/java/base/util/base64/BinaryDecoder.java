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
 * Defines common decoding methods for byte array decoders.
 *
 * @author Apache Software Foundation
 * @version $Id : BinaryDecoder.java,v 1.10 2004/06/15 18:14:15 ggregory Exp $
 */
public interface BinaryDecoder extends Decoder {

    /**
     * Decodes a byte array and returns the results as a byte array.
     *
     * @param pArray A byte array which has been encoded with the               appropriate encoder
     * @return a byte array that contains decoded content
     * @throws DecoderException A decoder exception is thrown                          if a Decoder encounters a failure condition during                          the decode process.
     */
    byte[] decode(byte[] pArray) throws DecoderException;
}  

