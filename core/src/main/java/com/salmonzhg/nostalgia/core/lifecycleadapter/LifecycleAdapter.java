package com.salmonzhg.nostalgia.core.lifecycleadapter;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * author: Salmon
 * date: 2017-06-21 16:17
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public abstract class LifecycleAdapter {

    private ActivityLifecycle lifecycle = ActivityLifecycle.CREATE;
    private final PublishSubject<ActivityLifecycle> subject = PublishSubject.create();
    private Disposable disposable;

    public final ActivityLifecycle getLifecycle() {
        return lifecycle;
    }

    protected final void changeLifecycle(ActivityLifecycle lifecycle) {
        this.lifecycle = lifecycle;
        if (subject.hasObservers()) {
            subject.onNext(lifecycle);
//            if (lifecycle == ActivityLifecycle.DESTROY) {
//                subject.onComplete();
//            }
        }
    }

    protected void bind(Disposable disposable) {
        this.disposable = disposable;
    }

    public void destroy() {
        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
        subject.onComplete();
    }

    public final Observable<ActivityLifecycle> lifecycle() {
        return subject.hide();
    }
}
