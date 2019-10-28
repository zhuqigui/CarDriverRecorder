package com.softwinner.bionrecorder.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.util.LruCache;

import com.softwinner.bionrecorder.util.Utils;

/**
 * @author zhongzhiwen
 * @date 2017/9/14
 * @email zhongzhiwen24@gmail.com
 */

public class ThumbCache {
    private static final String TAG = "ThumbCache";
    private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5; // 5MB


    private LruCache<String, Bitmap> mMemoryCache;
    private int mMemCacheSize = DEFAULT_MEM_CACHE_SIZE;

    public ThumbCache(Context context) {
        mMemoryCache = new LruCache<String, Bitmap>(mMemCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return Utils.getBitmapSize(value);
            }
        };
    }

    // 如果已经有ThumbsCache实例，那么直接返回，否则创建一个
    public static ThumbCache findOrCreateThumbsCache(FragmentActivity activity) {
        RetainFragment fragment = RetainFragment.findOrCreateRetainFragment(
                activity.getSupportFragmentManager());
        ThumbCache thumbCache = fragment.mThumbCache;
        if (thumbCache == null) {
            thumbCache = new ThumbCache(activity.getApplicationContext());
            fragment.mThumbCache = thumbCache;
        }

        return thumbCache;
    }

    /**
     * 从内存缓存中获取Bitmap
     * @param key
     * *
     * @return
     */
    public Bitmap getBitmapFromCache(String key) {
        Bitmap bitmap = null;

        if (mMemoryCache != null) {
            bitmap = mMemoryCache.get(key);
        }

        return bitmap;
    }


    /**
     * 将Bitmap存储到内存缓存中
     */
    public void putBitmpaIntoCache(String key, Bitmap bitmap) {
        if (mMemoryCache == null) {
            mMemoryCache = new LruCache<String, Bitmap>(mMemCacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return Utils.getBitmapSize(value);
                }
            };
        }

        mMemoryCache.put(key, bitmap);
    }
}
