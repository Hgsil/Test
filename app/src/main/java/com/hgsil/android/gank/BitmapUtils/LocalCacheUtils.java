package com.hgsil.android.gank.BitmapUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/3/8 0008.
 */

public class LocalCacheUtils {
    private static final String CACHE_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/Gank";

    /**
     * 从本地读取图片
     * @param url
     */
    public Bitmap getBitmapFromLocal(String url){
        try {
            File file=new File(CACHE_PATH,url);

            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从网络获取图片后,保存至本地缓存
     * @param url
     * @param bitmap
     */
    public void setBitmapToLocal(String url,Bitmap bitmap,boolean isDownLoad,Context context){
        if (isDownLoad){
            saveImageToGallery(context,bitmap,url);
        }
        else saveImage(bitmap,url);

    }
    //缓存图片
    public static void saveImage(Bitmap bmp,String url) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "Gank");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = url;
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //下载图片不需要压缩
    public  void saveImageToGallery(Context context, Bitmap bmp ,String url) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Gank");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = url;
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 0, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        if (!bmp.isRecycled()){
            bmp.recycle();
            System.gc();
        }
    }



}
