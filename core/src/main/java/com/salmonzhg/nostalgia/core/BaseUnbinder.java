package com.salmonzhg.nostalgia.core;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * author: Salmon
 * date: 2017-06-30 13:35
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class BaseUnbinder implements Unbinder {

    final protected Object bindTarget;

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    public BaseUnbinder(Object bindTarget) {
        this.bindTarget = bindTarget;
    }

    protected void bind(Disposable disposable) {
        this.compositeDisposable.add(disposable);
    }

    @Override
    public boolean isUnbound() {
        return compositeDisposable.isDisposed();
    }

    @Override
    public void unbind() {
        if (!isUnbound()) {
            compositeDisposable.clear();
        }
        Nostalgia.internal.removeBinding(bindTarget);
    }
}
