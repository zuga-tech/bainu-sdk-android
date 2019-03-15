package com.zuga.test;

import android.app.Application;

import com.zuga.bainu.BNApiFactory;

/**
 * @author saqrag
 * @version 1.0
 * @see null
 * 2016/9/23
 * @since 1.0
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BNApiFactory.init(this, "bn0428040730");
    }
}
