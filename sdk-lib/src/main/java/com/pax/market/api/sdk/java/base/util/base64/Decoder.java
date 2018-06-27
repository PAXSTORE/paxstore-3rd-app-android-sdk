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
 * <p>Provides the highest level of abstraction for Decoders.
 * This is the sister interface of {@link Encoder}.  All
 * Decoders implement this common generic interface.</p>
 * <p/>
 * <p>Allows a user to pass a generic Object to any Decoder
 * implementation in the codec package.</p>
 * <p/>
 * <p>One of the two interfaces at the center of the codec package.</p>
 *
 * @author Apache Software Foundation
 * @version $Id : Decoder.java,v 1.9 2004/02/29 04:08:31 tobrien Exp $
 */
public interface Decoder {

    /**
     * Decodes an "encoded" Object and returns a "decoded"
     * Object.  Note that the implementation of this
     * interface will try to cast the Object parameter
     * to the specific type expected by a particular Decoder
     * implementation.  If a {@link ClassCastException} occurs
     * this decode method will throw a DecoderException.
     *
     * @param pObject an object to "decode"
     * @return a 'decoded" object
     * @throws DecoderException a decoder exception can                          be thrown for any number of reasons.  Some good                          candidates are that the parameter passed to this                          method is null, a param cannot be cast to the                          appropriate type for a specific encoder.
     */
    Object decode(Object pObject) throws DecoderException;
}  

