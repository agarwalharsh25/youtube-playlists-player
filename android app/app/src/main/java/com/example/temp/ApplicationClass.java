package com.example.temp;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.support.multidex.MultiDex;

public class ApplicationClass extends Application {

    static ApplicationClass instance;

    public static ApplicationClass getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        instance = this;
    }
}
