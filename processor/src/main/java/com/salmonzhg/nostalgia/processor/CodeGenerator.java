package com.salmonzhg.nostalgia.processor;

import com.salmonzhg.nostalgia.core.Nostalgia;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;

/**
 * author: Salmon
 * date: 2017-06-27 14:40
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class CodeGenerator {

    private static Filer filer;

    public static void generate(List<Generator> generators, ProcessingEnvironment processingEnvironment) {
        CodeGenerator.filer = processingEnvironment.getFiler();

        for (Generator generator : generators) {
            try {
                generator.generateJavaFile().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        filer = null;
    }

    public static class Names {
        public static String GENERATE_CLASS_NAME_POSTFIX = Nostalgia.internal.GENERATE_CLASS_NAME_POSTFIX;
        public static String METHOD_GENERATEBINDING_PARAM_NAME = "bindTarget";
        public static String METHOD_GENERATEBINDING_LOCAL_PARAM_UNBINDER = "unbinder";
        public static String METHOD_GENERATEBINDING_LOCAL_PARAM_TARGET = "target";
    }
}
