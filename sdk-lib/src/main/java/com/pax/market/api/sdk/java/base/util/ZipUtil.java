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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * 通过Java的Zip输入输出流实现压缩和解压文件
 *
 * @author zcy
 */
public final class ZipUtil {
    private static final Logger logger = LoggerFactory.getLogger(ZipUtil.class);

    private ZipUtil() {
        // empty
    }

    /**
     * 压缩文件
     *
     * @param filePath 待压缩的文件路径
     * @return 压缩后的文件 file
     */
    public static File zip(String filePath) {
        File target = null;
        File source = new File(filePath);
        if (source.exists()) {
            // 压缩文件名=源文件名.zip
            String zipName = source.getName() + ".zip";
            target = new File(source.getParent(), zipName);
            if (target.exists()) {
                target.delete(); // 删除旧的文件
            }
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(target);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                // 添加对应的文件Entry
                addEntry("/", source, zos);
            } catch (IOException e) {
            	logger.error(String.format("error zip file '%s'", filePath), e);
            	return null;
            } finally {
                IOUtil.closeQuietly(zos, fos);
            }
        }
        return target;
    }

    /**
     * 扫描添加文件Entry
     *
     * @param base   基路径
     * @param source 源文件
     * @param zos    Zip文件输出流
     * @throws IOException
     */
    private static void addEntry(String base, File source, ZipOutputStream zos)
            throws IOException {
        // 按目录分级，形如：/aaa/bbb.txt
        String entry = base + source.getName();
        if (source.isDirectory()) {
            for (File file : source.listFiles()) {
                // 递归列出目录下的所有文件，添加文件Entry
                addEntry(entry + "/", file, zos);
            }
        } else {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                byte[] buffer = new byte[1024 * 10];
                fis = new FileInputStream(source);
                bis = new BufferedInputStream(fis, buffer.length);
                int read = 0;
                zos.putNextEntry(new ZipEntry(entry));
                while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
                    zos.write(buffer, 0, read);
                }
                zos.closeEntry();
            } finally {
                IOUtil.closeQuietly(bis, fis);
            }
        }
    }

    /**
     * 解压文件
     *
     * @param filePath 压缩文件路径
     */
    public static boolean unzip(String filePath) {
        File source = new File(filePath);
        if (source.exists()) {
            ZipInputStream zis = null;
            FileInputStream fis = null;


            try {
                fis = new FileInputStream(source);
                zis = new ZipInputStream(fis);
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null && !entry.isDirectory()) {
                    writeEachFile(source, zis, entry);
                }
                zis.closeEntry();
            } catch (IOException e) {
                logger.error(String.format("error write single file %s", filePath), e);
                return false;
            } finally {
                IOUtil.closeQuietly(zis, fis);
            }
        }

        return true;
    }

    private static void writeEachFile(File source, ZipInputStream zis, ZipEntry entry) {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            File target = new File(source.getParent(), entry.getName());
            if (!target.getParentFile().exists()) {
                // 创建文件父目录
                target.getParentFile().mkdirs();
            }
            // 写入文件
            fos = new FileOutputStream(target);
            bos = new BufferedOutputStream(fos);
            int read = 0;
            byte[] buffer = new byte[1024 * 10];
            while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, read);
            }
            bos.flush();
        } catch (IOException e) {
            logger.error("error writeEachFile", e);
        } finally {
            IOUtil.closeQuietly(fos, bos);
        }
    }

}
