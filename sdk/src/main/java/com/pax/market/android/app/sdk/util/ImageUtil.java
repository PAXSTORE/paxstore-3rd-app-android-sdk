package com.pax.market.android.app.sdk.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageUtil {

    public static boolean saveImage(Bitmap photo, String spath) {
        try {
            // 文件或目录不存在时,创建目录和文件.
            File file = new File(spath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(spath, false));
            photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Get pictures from the corresponding catalog
     *
     * @param filePath
     * @param context
     * @return
     */
    public static Bitmap getBitmapFromFile(String filePath, Context context) {
        File rFile = new File(filePath);
        if (rFile.exists() && rFile.canRead()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(rFile);
                BitmapDrawable bd = new BitmapDrawable(fis);
                Bitmap newBitmap = bd.getBitmap();
                return newBitmap;
            } catch (IOException e) {
                Log.e("ImageUtil", e.getMessage(), e);
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // swallow
                    }
                }
            }
        }
        return null;
    }

    public static Bitmap getBitmapFromUrl(String url) throws IOException {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//            bitmap = BitmapFactory.decodeStream(is, null, options);
            bitmap = getFitSampleBitmap(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }



    public  static  Bitmap  getFitSampleBitmap(InputStream  inputStream) throws Exception{
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        byte[] bytes = readStream(inputStream);
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        int i = bytes.length / 1024 / 1024; // 1M以内不缩放
        Log.d("ImageUtil", "bytes.length:" + bytes.length);
        Log.d("ImageUtil", "缩小的比例 i:" + i);
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * Get byte stream from inputStream array size
     **/
    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }


}
