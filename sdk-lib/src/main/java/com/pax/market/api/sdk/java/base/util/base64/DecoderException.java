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
 * Thrown when a Decoder has encountered a failure condition during a decode.
 *
 * @author Apache Software Foundation
 * @version $Id : DecoderException.java,v 1.9 2004/02/29 04:08:31 tobrien Exp $
 */
public class DecoderException extends Exception {
    private static final long serialVersionUID = 7623166766451083981L;

    /**
     * Creates a DecoderException
     *
     * @param pMessage A message with meaning to a human
     */
    public DecoderException(String pMessage) {
        super(pMessage);
    }

}  

