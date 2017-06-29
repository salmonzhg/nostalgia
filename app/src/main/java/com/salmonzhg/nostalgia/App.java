package com.salmonzhg.nostalgia;

import android.app.Application;

import com.salmonzhg.nostalgia.core.Nostalgia;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * author: Salmon
 * date: 2017-06-29 23:27
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Nostalgia.initialize(AndroidSchedulers.mainThread());
    }
}
