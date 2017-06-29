package com.salmonzhg.nostalgia.core;

/**
 * author: Salmon
 * date: 2017-06-28 17:40
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class Event {
    private String tag;
    private Object data;

    public Event(String tag) {
        this(tag, new EmptyContent());
    }

    public Event(String tag, Object data) {
        this.tag = tag;
        this.data = data;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
