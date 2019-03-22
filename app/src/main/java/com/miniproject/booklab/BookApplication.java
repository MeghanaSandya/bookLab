package com.miniproject.booklab;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

public class BookApplication extends Application {

    private static Context mContext;
    private static Gson mGsonInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getBaseContext();
        mGsonInstance = new Gson();
    }

    public static Context getAppContext() {
        return mContext;
    }

    public static Gson getGsonInstance() {
        return mGsonInstance;
    }
}