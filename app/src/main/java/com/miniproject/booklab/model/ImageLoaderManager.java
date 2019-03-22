package com.miniproject.booklab.model;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.miniproject.booklab.BookApplication;

public class ImageLoaderManager {
    private final static int MAX_CACHE_SIZE_IN_BYTES = 10 * 1024 * 1024;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static ImageLoaderManager mInstance;

    private ImageLoaderManager() {
    }

    public static synchronized ImageLoaderManager getInstance() {
        if (mInstance == null) {
            mInstance = new ImageLoaderManager();
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(BookApplication.getAppContext().getCacheDir(), MAX_CACHE_SIZE_IN_BYTES);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        this.mRequestQueue = getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue,
                    new ImageLoader.ImageCache() {
                        private final LruCache<String, Bitmap>
                                cache = new LruCache<String, Bitmap>(getDefaultLruCacheSize());

                        @Override
                        public Bitmap getBitmap(String url) {
                            return cache.get(url);
                        }

                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            cache.put(url, bitmap);
                        }
                    });
        }
        return this.mImageLoader;
    }

    private int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        return maxMemory / 8;
    }
}
