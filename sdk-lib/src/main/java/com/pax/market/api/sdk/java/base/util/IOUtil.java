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

package com.pax.market.api.sdk.java.base.util;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * IO流工具类
 *
 * @author zcy
 */
public class IOUtil {
    private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);

    /**
     * 关闭一个或多个流对象
     *
     * @param closeables 可关闭的流对象列表
     * @throws IOException the io exception
     */
    public static void close(Closeable... closeables) throws IOException {
        if (closeables != null) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        }
    }

    /**
     * 关闭一个或多个流对象
     *
     * @param closeables 可关闭的流对象列表
     */
    public static void closeQuietly(Closeable... closeables) {
        try {
            close(closeables);
        } catch (IOException e) {
            logger.error("IOException Occurred. Details: {}", e.toString());
        }
    }
 
}