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
 * Thrown when there is a failure condition during the encoding process.  This
 * exception is thrown when an Encoder encounters a encoding specific exception
 * such as invalid data, inability to calculate a checksum, characters outside of the
 * expected range.
 *
 * @author Apache Software Foundation
 * @version $Id : EncoderException.java,v 1.10 2004/02/29 04:08:31 tobrien Exp $
 */
public class EncoderException extends Exception {
    private static final long serialVersionUID = -4119445989502658917L;

    /**
     * Creates a new instance of this exception with an useful message.
     *
     * @param pMessage a useful message relating to the encoder specific error.
     */
    public EncoderException(String pMessage) {
        super(pMessage);
    }
}  

