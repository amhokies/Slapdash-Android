package com.virginiatech.slapdash.slapdash.InvitingFriendList_Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Created by Nima on 10/24/16.
 * <p>
 * The bitmap cache used to cache the result of the request.
 * It is mostly used to store the picture retrieved from http
 * requests.
 */
public class LruBitmapCache extends LruCache<String, Bitmap>
        implements ImageCache {
    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Public Methods                                          //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public LruBitmapCache(int maxSize) {
        super(maxSize);
    }

    //-------------------------------------------------------------------------------------------
    public LruBitmapCache(Context ctx) {
        this(getCacheSize(ctx));
    }

    //-------------------------------------------------------------------------------------------
    /**
     * Returns a cache size equal to approximately three screens worth of images.
     */
    public static int getCacheSize(Context ctx) {
        final DisplayMetrics displayMetrics = ctx.getResources().
                getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;
        // 4 bytes per pixel
        final int screenBytes = screenWidth * screenHeight * 4;

        return screenBytes * 3;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //                                 Overrides                                               //
    /////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}