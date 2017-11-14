package com.hgsil.android.gank.BitmapUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hgsil.android.gank.R;

/**
 * Created by Administrator on 2017/3/8 0008.
 */

public class MyBitmapUtils {
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private Context mContext;
    private boolean isDownLoad =false;

    public MyBitmapUtils(Context context){
        mMemoryCacheUtils=new MemoryCacheUtils();
        mLocalCacheUtils=new LocalCacheUtils();
        mNetCacheUtils=new NetCacheUtils(mLocalCacheUtils,mMemoryCacheUtils);
        mContext = context;

    }

    public void setDownLoad(boolean downLoad) {
        isDownLoad = downLoad;
    }

    public void disPlay(ImageView ivPic, String url) {
        Bitmap bitmap;
        //内存缓存
        bitmap=mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap!=null){
            ivPic.setImageBitmap(bitmap);
            System.out.println("从内存获取图片啦.....");
            return;
        }

        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if(bitmap !=null){
            ivPic.setImageBitmap(bitmap);
            System.out.println("从本地获取图片啦.....");
            //从本地获取图片后,保存至内存中
            mMemoryCacheUtils.setBitmapToMemory(url,bitmap);
            return;
        }
        //网络缓存
        mNetCacheUtils.getBitmapFromNet(ivPic,url,mContext,isDownLoad);
    }
    public void downLoad(ImageView ivPic, String url){
        mNetCacheUtils.getBitmapFromNet(ivPic,url,mContext,isDownLoad);
    }
}
