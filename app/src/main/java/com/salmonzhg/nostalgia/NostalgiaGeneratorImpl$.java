package com.salmonzhg.nostalgia;

import com.salmonzhg.nostalgia.core.BaseLifecycleUnbinder;
import com.salmonzhg.nostalgia.core.Event;
import com.salmonzhg.nostalgia.core.Nostalgia;
import com.salmonzhg.nostalgia.core.annotation.Scheduler;
import com.salmonzhg.nostalgia.core.lifecycleadapter.ActivityLifecycle;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * this is for experiment
 * author: Salmon
 * date: 2017-06-28 13:34
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class NostalgiaGeneratorImpl$ extends BaseLifecycleUnbinder {

    public NostalgiaGeneratorImpl$(final Object bindTarget) {
        super(bindTarget);

        if (bindTarget.getClass().getCanonicalName().equals("com.salmonzhg.nostalgia.MainActivity")) {
            bind(Nostalgia.toObservable("tag")
                    .observeOn(Nostalgia.internal.resolveSchedulers(Scheduler.MAINTHREAD))
                    .filter(lifecycleFilter(ActivityLifecycle.CREATE, ActivityLifecycle.DESTROY))
                    .subscribe(new Consumer<Event>() {
                        @Override
                        public void accept(Event event) throws Exception {
                            MainActivity target = (MainActivity) bindTarget;
                            if (event.getData() instanceof String)
                                target.onReceived((String) event.getData());
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
    }

}
