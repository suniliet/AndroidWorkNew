package com.kns.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader;
import android.util.DisplayMetrics;

/**
 * copied from official documentation
 */
public class MyLruBitmapCache extends LruCache<String, Bitmap>
        implements ImageLoader.ImageCache {

    public MyLruBitmapCache(int maxSize) {
        super(maxSize);
    }

    public MyLruBitmapCache(Context ctx) {
      //  this(getCacheSize(ctx));
    	this(getDefaultLruCacheSize());
    }

    @SuppressLint("NewApi")
	@Override
    protected int sizeOf(String key, Bitmap value) {
    	//return value.getRowBytes() * value.getHeight();
       // return value.getRowBytes() * value.getHeight()/ 1024;
        return value.getByteCount() / 1024;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }

    // Returns a cache size equal to approximately three screens worth of images. this give OOM Exception
    public static int getCacheSize(Context ctx) {
        final DisplayMetrics displayMetrics = ctx.getResources().
                getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;
        // 4 bytes per pixel
        final int screenBytes = screenWidth * screenHeight * 4;

        return screenBytes * 3;
    }
    
    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }
}
