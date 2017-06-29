package com.salmonzhg.nostalgia.processor;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.salmonzhg.nostalgia.processor.step.ReceiveStep;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;

/**
 * author: Salmon
 * date: 2017-06-14 23:28
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

@AutoService(Processor.class)
public class NostalgiaProcessor extends BasicAnnotationProcessor {

    public static List<NostalgiaConfig> configs = new ArrayList<>();
    private boolean hasGenerated = false;

    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        return ImmutableSet.of(
                new ReceiveStep()
        );
    }

    @Override
    protected void postRound(RoundEnvironment roundEnv) {
        super.postRound(roundEnv);
        if (!hasGenerated) {
            CodeGenerator.generate(configs, processingEnv.getFiler());
            hasGenerated = true;
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
