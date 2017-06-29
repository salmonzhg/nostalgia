package com.salmonzhg.nostalgia.processor;

import com.salmonzhg.nostalgia.core.EmptyContent;
import com.salmonzhg.nostalgia.core.Event;
import com.salmonzhg.nostalgia.core.INostalgiaGenerator;
import com.salmonzhg.nostalgia.core.Nostalgia;
import com.salmonzhg.nostalgia.core.Unbinder;
import com.salmonzhg.nostalgia.core.annotation.Scheduler;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * author: Salmon
 * date: 2017-06-27 14:40
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class CodeGenerator {

    static class Names {
        static String GENERATE_PACKAGE_NAME = Nostalgia.internal.GENERATE_PACKAGE_NAME;
        static String GENERATE_CLASS_NAME = Nostalgia.internal.GENERATE_CLASS_NAME;
        static String METHOD_GENERATEBINDING_PARAM_NAME = "bindTarget";
        static String METHOD_GENERATEBINDING_LOCAL_PARAM_UNBINDER = "unbinder";
        static String METHOD_GENERATEBINDING_LOCAL_PARAM_TARGET = "target";
    }

    private static List<NostalgiaConfig> configs;
    private static Filer filer;

    public static void generate(List<NostalgiaConfig> configs, Filer filer) {
        CodeGenerator.configs = configs;
        CodeGenerator.filer = filer;

        try {
            javaFile().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JavaFile javaFile() {
        return JavaFile.builder(Names.GENERATE_PACKAGE_NAME, typeSpec())
                .addStaticImport(Scheduler.MAINTHREAD)
                .addStaticImport(Scheduler.IO)
                .addStaticImport(Scheduler.NEWTHREAD)
                .addStaticImport(Scheduler.COMPUTATION)
                .build();
    }

    private static TypeSpec typeSpec() {
        return TypeSpec.classBuilder(Names.GENERATE_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(INostalgiaGenerator.class)
                .addMethod(generateBindingMethodSpec())
                .build();
    }

    private static MethodSpec generateBindingMethodSpec() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("generateBinding")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Unbinder.class)
                .addParameter(Object.class, Names.METHOD_GENERATEBINDING_PARAM_NAME, Modifier.FINAL)
                .addStatement("final $T "+ Names.METHOD_GENERATEBINDING_LOCAL_PARAM_UNBINDER + " = new Unbinder(" + Names.METHOD_GENERATEBINDING_PARAM_NAME +")", Unbinder.class);

        for (NostalgiaConfig config : configs) {
            addDetailStatementForGenerateBindingMethodSpec(config, builder);
        }

        return builder.addStatement("return " + Names.METHOD_GENERATEBINDING_LOCAL_PARAM_UNBINDER).build();
    }

    private static void addDetailStatementForGenerateBindingMethodSpec(NostalgiaConfig config, MethodSpec.Builder builder) {//descriptor.methodElement.enclosingElement.asType()
        builder.beginControlFlow("if(" + Names.METHOD_GENERATEBINDING_PARAM_NAME +".getClass().getCanonicalName().equals(\"" + config.getElement().getEnclosingElement().asType() + "\"))")
                .addStatement(Names.METHOD_GENERATEBINDING_LOCAL_PARAM_UNBINDER + ".bind(" +
                                getNostalgiaStr() +
                                getSubscibeCodeStr(config) +
                                getObserverOnThreadStr(config) +
                                getSubscribeStr(config) +
                                ")"
                )
                .endControlFlow();
    }

    private static String getSubscribeStr(NostalgiaConfig config) {
        return  ".subscribe(" +
                onNextBlockSpec(config) +
                "," +
                onErrorBlockSpec() +
                ","+
                onCompleteBlockSpec() +
                ")";
    }

    private static String onErrorBlockSpec() {
        return  CodeBlock.of("new $T<Throwable>() {", Consumer.class)
                +
                MethodSpec.methodBuilder("accept")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(Throwable.class, "t")
                .addException(Exception.class)
                .build().toString()
                +
                "}";
    }

    private static String onCompleteBlockSpec() {
        return CodeBlock.of("new $T() {", Action.class) +
                MethodSpec.methodBuilder("run")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .build().toString()
                +"}";
    }

    private static String onNextBlockSpec(NostalgiaConfig  config) {
        return  CodeBlock.of("new $T<$T>() {", Consumer.class, Event.class)
                +
                MethodSpec.methodBuilder("accept")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .addParameter(Event.class, "event")
                        .addCode(getReceiveInvokeStr(config))
                        .addException(Exception.class)
                        .build().toString()
                +
                "}";
    }

    private static String getSubscibeCodeStr(NostalgiaConfig config) {
        return ".toObservable(\"" + config.getTag() +"\")";
    }

    private static String getNostalgiaStr() {
        return Nostalgia.class.getName();
    }

    private static String getObserverOnThreadStr(NostalgiaConfig config) {
        return ".observeOn(" + getNostalgiaStr() + ".internal.resolveSchedulers(" + config.getThread().toString() +"))";
    }

    private static String getReceiveInvokeStr(NostalgiaConfig config) {
        CodeBlock.Builder builder = CodeBlock
                .builder()
                .addStatement("final $T " + Names.METHOD_GENERATEBINDING_LOCAL_PARAM_TARGET + "=($T)" + Names.METHOD_GENERATEBINDING_PARAM_NAME,
                        config.getElement().getEnclosingElement().asType(),
                        config.getElement().getEnclosingElement().asType()
                );
        if (config.getElement().getParameters() == null || config.getElement().getParameters().size() == 0) {
            builder.beginControlFlow("if(event.getData() instanceof $T)", EmptyContent.class);
            builder.addStatement(Names.METHOD_GENERATEBINDING_LOCAL_PARAM_TARGET +"."+config.getElement().getSimpleName()+"()");
        } else {
            TypeMirror typeMirror = config.getElement().getParameters().get(0).asType();
            builder.beginControlFlow("if(event.getData() instanceof " + getType(typeMirror) + ")");
            builder.addStatement(Names.METHOD_GENERATEBINDING_LOCAL_PARAM_TARGET + "." + config.getElement().getSimpleName() + "(($T) event.getData())", typeMirror);
        }
        builder.endControlFlow();
        return builder.build().toString();
    }

    private static String getType(TypeMirror typeMirror) {
        TypeKind typeKind = typeMirror.getKind();
        switch (typeKind) {
            case BOOLEAN:
                return "Boolean";
            case BYTE:
                return "Byte";
            case SHORT:
                return "Short";
            case INT:
                return "Integer";
            case LONG:
                return "Long";
            case CHAR:
                return "Char";
            case FLOAT:
                return "Float";
            case DOUBLE:
                return "Double";
        }
        if (typeMirror instanceof DeclaredType) {
            return ((TypeElement) ((DeclaredType) typeMirror).asElement()).getQualifiedName().toString();
        } else {
            return typeMirror.toString();
        }
    }
}
