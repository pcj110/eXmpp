package com.cn.lx;

import android.app.Application;

import org.litepal.LitePal;
import org.litepal.LitePalDB;

/**
 * Created by xueliang on 2017/4/14.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
