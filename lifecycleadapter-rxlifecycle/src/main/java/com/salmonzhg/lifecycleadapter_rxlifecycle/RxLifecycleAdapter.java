package com.salmonzhg.lifecycleadapter_rxlifecycle;

import com.salmonzhg.nostalgia.core.lifecycleadapter.ActivityLifecycle;
import com.salmonzhg.nostalgia.core.lifecycleadapter.LifecycleAdapter;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
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

    private Observable<ActivityEvent> lifecycleObserver;

    public RxLifecycleAdapter(Observable<ActivityEvent> lifecycleObserver) {
        this.lifecycleObserver = lifecycleObserver;

        bind(this.lifecycleObserver
                .takeUntil(new Predicate<ActivityEvent>() {
                    @Override
                    public boolean test(@NonNull ActivityEvent activityEvent) throws Exception {
                        return activityEvent == ActivityEvent.DESTROY;
                    }
                })
                .subscribe(new Consumer<ActivityEvent>() {
                    @Override
                    public void accept(@NonNull ActivityEvent activityEvent) throws Exception {
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

    public static final ActivityLifecycle parse(ActivityEvent activityEvent) {
        ActivityLifecycle lifecycle;
        if (activityEvent == ActivityEvent.CREATE) {
            lifecycle = ActivityLifecycle.CREATE;
        } else if (activityEvent == ActivityEvent.START) {
            lifecycle = ActivityLifecycle.START;
        } else if (activityEvent == ActivityEvent.RESUME) {
            lifecycle = ActivityLifecycle.RESUME;
        } else if (activityEvent == ActivityEvent.PAUSE) {
            lifecycle = ActivityLifecycle.PAUSE;
        } else if (activityEvent == ActivityEvent.STOP) {
            lifecycle = ActivityLifecycle.STOP;
        } else if (activityEvent == ActivityEvent.DESTROY) {
            lifecycle = ActivityLifecycle.DESTROY;
        } else {
            lifecycle = ActivityLifecycle.CREATE;
        }
        return lifecycle;
    }
}
