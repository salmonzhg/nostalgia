package com.salmonzhg.nostalgia.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * author: Salmon
 * date: 2017-06-29 23:29
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Take {
    int times();
}
