package com.salmonzhg.nostalgia.processor.step;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.salmonzhg.nostalgia.core.annotation.Receive;
import com.salmonzhg.nostalgia.core.annotation.Take;
import com.salmonzhg.nostalgia.processor.NostalgiaConfig;
import com.salmonzhg.nostalgia.processor.NostalgiaProcessor;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 * author: Salmon
 * date: 2017-06-30 11:14
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class TakeStep extends BaseStep {
    public TakeStep(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.of(Take.class);
    }

    @Override
    public Set<Element> process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        Set<Map.Entry<Class<? extends Annotation>, Collection<Element>>> entries = elementsByAnnotation.asMap().entrySet();
        Iterator<Map.Entry<Class<? extends Annotation>, Collection<Element>>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Class<? extends Annotation>, Collection<Element>> entry = iterator.next();
            Collection<Element> value = entry.getValue();
            Iterator<Element> iterator1 = value.iterator();
            while (iterator1.hasNext()) {
                ExecutableElement executableElement = (ExecutableElement) iterator1.next();

                String canonicalName = executableElement.getEnclosingElement().asType().toString();

                if (!NostalgiaProcessor.generatorMap.containsKey(canonicalName)) continue;

                Map<Element, NostalgiaConfig> configMap = NostalgiaProcessor.generatorMap.get(canonicalName).getConfigMap();

                if (!configMap.containsKey(executableElement)) continue;

                NostalgiaConfig config = configMap.get(executableElement);

                config.setTakeTimes(MoreElements.asExecutable(executableElement).getAnnotation(Take.class).times());
            }
        }
        return new HashSet<>();
    }
}
