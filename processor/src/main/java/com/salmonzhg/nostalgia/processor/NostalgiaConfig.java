package com.salmonzhg.nostalgia.processor;

import com.salmonzhg.nostalgia.core.annotation.Scheduler;

import javax.lang.model.element.ExecutableElement;

/**
 * author: Salmon
 * date: 2017-06-29 15:19
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class NostalgiaConfig {

    private String tag;
    private Scheduler thread;
    private int takeTimes;
    private ExecutableElement element;

    public NostalgiaConfig(ExecutableElement element) {
        this.element = element;
    }

    public int getTakeTimes() {
        return takeTimes;
    }

    public void setTakeTimes(int takeTimes) {
        this.takeTimes = takeTimes;
    }

    public Scheduler getThread() {
        return thread;
    }

    public void setThread(Scheduler thread) {
        this.thread = thread;
    }

    public ExecutableElement getElement() {
        return element;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
