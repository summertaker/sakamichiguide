package com.summertaker.sakamichiguide.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import com.summertaker.sakamichiguide.common.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {
    private static final String mTAG = "========== ImageUtil";

    public static void saveBitmapToPng(Bitmap bitmap, String fileName) {
        fileName = getCacheFileName(fileName);
        saveBitmap(bitmap, fileName, Bitmap.CompressFormat.PNG);
    }

    //public static String saveBitmapToJpg(Bitmap bitmap, String fileName) {
    //    fileName = getFileName(fileName) + ".jpg";
    //    return saveBitmap(bitmap, fileName, Bitmap.CompressFormat.JPEG);
    //}

    public static void saveBitmap(final Bitmap bitmap, final String fileName, final Bitmap.CompressFormat format) {
        File dir = new File(getCachePah());
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            bitmap.setHasAlpha(true);
            bitmap.compress(format, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCachePah() {
        String storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        return storage + Config.CACHE_IMAGE_SAVE_PATH;
    }

    public static String getCacheFileName(String fileName) {
        //return getCachePah() + Util.encodeBase64(fileName) + ".png";
        return getCachePah() + fileName + ".png";
    }

    public static String getValidCacheUri(String id) {
        String fileName = getCacheFileName(id);

        File file = new File(fileName);

        if (file.exists()) {
            String fileUri = Uri.fromFile(file).toString();
            return Uri.decode(fileUri);
        } else {
            return null;
        }
    }

    /*public static DisplayImageOptions getDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                        //.showImageOnLoading(R.drawable.transparent)
                        //.showImageForEmptyUri(R.drawable.transparent)
                        //.showImageOnFail(R.drawable.transparent)
                .build();
    }

    public static DisplayImageOptions getDisplayImageOptionsSize(final int width, final int height) {
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.transparent)
                .showImageForEmptyUri(R.drawable.transparent)
                .showImageOnFail(R.drawable.transparent)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, width, height, false);
                    }
                })
                .build();
    }*/
}
