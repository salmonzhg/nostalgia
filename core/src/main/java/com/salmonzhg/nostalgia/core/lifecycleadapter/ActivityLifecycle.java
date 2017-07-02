package com.salmonzhg.nostalgia.core.lifecycleadapter;

/**
 * author: Salmon
 * date: 2017-06-21 16:18
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public enum ActivityLifecycle {
    UNDEFINED(-1),
    CREATE(0),
    START(1),
    RESUME(2),
    PAUSE(3),
    STOP(4),
    DESTROY(5);

    private int flag;

    ActivityLifecycle(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}
