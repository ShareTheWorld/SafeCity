package com.city.safe.activity;

import android.app.Application;

import com.city.safe.utils.Logger;

import org.xutils.x;


/**
 * Created by user on 16-11-23.
 */

public class BootApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("hongtao.fu","start BootApplication");
        x.Ext.init(this); // 这一步之后, 我们就可以在任何地方使用x.app()来获取Application的实例了.
        x.Ext.setDebug(true); // 是否输出debug日志
    }

}
