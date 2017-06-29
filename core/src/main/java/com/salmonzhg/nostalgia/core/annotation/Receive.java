package com.salmonzhg.nostalgia.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * author: Salmon
 * date: 2017-06-14 22:24
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Receive {
    String tag();
    Scheduler scheduler() default Scheduler.MAINTHREAD;
}
