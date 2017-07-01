package com.salmonzhg.nostalgia.core;

/**
 * author: Salmon
 * date: 2017-06-30 11:47
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public interface Unbinder {

    boolean isUnbound();

    void unbind();

    Unbinder EMPTY = new Unbinder() {
        @Override
        public boolean isUnbound() {return false;}

        @Override public void unbind() { }
    };
}
