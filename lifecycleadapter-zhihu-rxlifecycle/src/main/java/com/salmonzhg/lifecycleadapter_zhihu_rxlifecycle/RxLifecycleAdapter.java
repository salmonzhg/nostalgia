package com.salmonzhg.lifecycleadapter_zhihu_rxlifecycle;

import android.app.Activity;

import com.salmonzhg.nostalgia.core.lifecycleadapter.ActivityLifecycle;
import com.salmonzhg.nostalgia.core.lifecycleadapter.LifecycleAdapter;

import cn.nekocode.rxlifecycle.LifecyclePublisher;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * author: Salmon
 * date: 2017-06-21 16:34
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class RxLifecycleAdapter extends LifecycleAdapter {

    private Observable<Integer> lifecycleObserver;

    public RxLifecycleAdapter(Activity activity) {

        lifecycleObserver = RxLifecycle.bind(activity).asObservable();

        bind(this.lifecycleObserver
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer activityEvent) throws Exception {
                        changeLifecycle(parse(activityEvent));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                }));
    }

    public static final ActivityLifecycle parse(Integer activityEvent) {
        ActivityLifecycle lifecycle;
        if (activityEvent == LifecyclePublisher.ON_CREATE) {
            lifecycle = ActivityLifecycle.CREATE;
        } else if (activityEvent == LifecyclePublisher.ON_START) {
            lifecycle = ActivityLifecycle.START;
        } else if (activityEvent == LifecyclePublisher.ON_RESUME) {
            lifecycle = ActivityLifecycle.RESUME;
        } else if (activityEvent == LifecyclePublisher.ON_PAUSE) {
            lifecycle = ActivityLifecycle.PAUSE;
        } else if (activityEvent == LifecyclePublisher.ON_STOP) {
            lifecycle = ActivityLifecycle.STOP;
        } else if (activityEvent == LifecyclePublisher.ON_DESTROY) {
            lifecycle = ActivityLifecycle.DESTROY;
        } else {
            lifecycle = ActivityLifecycle.CREATE;
        }
        return lifecycle;
    }
}
