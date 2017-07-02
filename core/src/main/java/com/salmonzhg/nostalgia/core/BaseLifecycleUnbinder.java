package com.salmonzhg.nostalgia.core;

import com.salmonzhg.nostalgia.core.lifecycleadapter.ActivityLifecycle;
import com.salmonzhg.nostalgia.core.lifecycleadapter.LifecycleAdapter;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Predicate;

/**
 * author: Salmon
 * date: 2017-06-30 13:35
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class BaseLifecycleUnbinder extends BaseUnbinder {

    protected LifecycleAdapter lifecycleAdapter;

    public BaseLifecycleUnbinder(Object bindTarget) {
        super(bindTarget);
    }

    public void setLifecycleAdapter(LifecycleAdapter lifecycleAdapter) {
        this.lifecycleAdapter = lifecycleAdapter;
    }

    protected Predicate<Event> lifecycleFilter(final ActivityLifecycle from, final ActivityLifecycle to) {
        return new Predicate<Event>() {
            @Override
            public boolean test(@NonNull Event event) throws Exception {
                if (lifecycleAdapter == null) return true;
                if (from != ActivityLifecycle.UNDEFINED
                        && lifecycleAdapter.getLifecycle().getFlag() < from.getFlag())
                    return false;
                if (to != ActivityLifecycle.UNDEFINED
                        && lifecycleAdapter.getLifecycle().getFlag() > to.getFlag())
                    return false;
                return true;
            }
        };
    }

    @Override
    public void unbind() {
        if (lifecycleAdapter != null) lifecycleAdapter.destroy();
        super.unbind();
    }
}
