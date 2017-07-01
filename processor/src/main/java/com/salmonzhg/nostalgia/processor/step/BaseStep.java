package com.salmonzhg.nostalgia.processor.step;

import com.google.auto.common.BasicAnnotationProcessor;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * author: Salmon
 * date: 2017-06-30 16:45
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public abstract class BaseStep implements BasicAnnotationProcessor.ProcessingStep {
    protected ProcessingEnvironment processingEnvironment;

    public BaseStep(ProcessingEnvironment processingEnvironment) {
        this.processingEnvironment = processingEnvironment;
    }
}
