package com.example.android.moviespart2_v1;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import static android.R.attr.tag;

/**
 * Created by waiyi on 9/10/2017.
 * This class implements a RequestQueue and uses a singleton pattern
 * so only one request queue exists for the live of the application.
 */

public class VolleySingleton {

    private static final String TAG = "Volleysingleton";

    private static VolleySingleton mVolleySingleton;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mContext;


    private VolleySingleton(Context context) {
        Log.d(TAG, "in singleton constructor");

        mContext = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache(){
                    private final LruCache<String, Bitmap>
                    cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url){
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap){
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized VolleySingleton getInstance(Context context){
        if (mVolleySingleton == null){
            mVolleySingleton = new VolleySingleton(context);

        }
        return mVolleySingleton;
    }

    public RequestQueue getRequestQueue(){
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req)
    {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    /**
    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(tag);
        getRequestQueue().add(req);
    }
     **/

    public ImageLoader getImageLoader(){
        return mImageLoader;
    }

    public void cancelPendingRequests(Object tag){
        if(mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}