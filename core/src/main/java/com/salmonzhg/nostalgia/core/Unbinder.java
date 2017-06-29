package com.salmonzhg.nostalgia.core;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * author: Salmon
 * date: 2017-06-29 11:39
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class Unbinder {

    private Object bindTarget;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public Unbinder(Object bindTarget) {
        this.bindTarget = bindTarget;
    }

    public void bind(Disposable disposable) {
        this.compositeDisposable.add(disposable);
    }

    public boolean isUnbinded() {
        return compositeDisposable.isDisposed();
    }

    public void unBind() {
        if (!isUnbinded()) {
            compositeDisposable.clear();
        }
        Nostalgia.unbindInternal(bindTarget);
    }

    static Unbinder EMPTY(Object bindTarget) {
        return new Unbinder(bindTarget);
    }
}
