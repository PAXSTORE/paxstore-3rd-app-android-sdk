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
 * <p>Provides the highest level of abstraction for Encoders.
 * This is the sister interface of {@link Decoder}.  Every implementation of
 * Encoder provides this common generic interface whic allows a user to pass a
 * generic Object to any Encoder implementation in the codec package.</p>
 *
 * @author Apache Software Foundation
 * @version $Id : Encoder.java,v 1.10 2004/02/29 04:08:31 tobrien Exp $
 */
public interface Encoder {

    /**
     * Encodes an "Object" and returns the encoded content
     * as an Object.  The Objects here may just be <code>byte[]</code>
     * or <code>String</code>s depending on the implementation used.
     *
     * @param pObject An object ot encode
     * @return An "encoded" Object
     * @throws EncoderException an encoder exception is                          thrown if the encoder experiences a failure                          condition during the encoding process.
     */
    Object encode(Object pObject) throws EncoderException;
}  

