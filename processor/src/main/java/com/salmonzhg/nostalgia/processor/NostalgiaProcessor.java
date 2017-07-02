package com.salmonzhg.nostalgia.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.salmonzhg.nostalgia.processor.step.LifecycleStep;
import com.salmonzhg.nostalgia.processor.step.ReceiveStep;
import com.salmonzhg.nostalgia.processor.step.TakeStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;

/**
 * author: Salmon
 * date: 2017-06-14 23:28
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

@AutoService(Processor.class)
public class NostalgiaProcessor extends BasicAnnotationProcessor {
    private boolean hasGenerated = false;

    public static Map<String, Generator> generatorMap = new HashMap<>();

    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        return ImmutableSet.of(
                new ReceiveStep(processingEnv),
                new TakeStep(processingEnv),
                new LifecycleStep(processingEnv)
        );
    }

    @Override
    protected void postRound(RoundEnvironment roundEnv) {
        super.postRound(roundEnv);
        if (!hasGenerated) {
            CodeGenerator.generate(new ArrayList<Generator>(generatorMap.values()), processingEnv);
            hasGenerated = true;
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
