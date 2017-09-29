package com.salmonzhg.nostalgia.processor;

import com.salmonzhg.nostalgia.core.BaseLifecycleUnbinder;
import com.salmonzhg.nostalgia.core.BaseUnbinder;
import com.salmonzhg.nostalgia.core.EmptyContent;
import com.salmonzhg.nostalgia.core.Event;
import com.salmonzhg.nostalgia.core.Nostalgia;
import com.salmonzhg.nostalgia.core.annotation.Scheduler;
import com.salmonzhg.nostalgia.core.lifecycleadapter.ActivityLifecycle;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * author: Salmon
 * date: 2017-07-01 23:46
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class Generator {
    private String packageStr;
    private String classStr;
    private String canonicalName;
    private TypeMirror typeMirror;

    public void setTypeMirror(TypeMirror typeMirror) {
        this.typeMirror = typeMirror;
    }

    private Map<Element, NostalgiaConfig> configMap = new HashMap<>();
    private List<NostalgiaConfig> configList;

    public Map<Element, NostalgiaConfig> getConfigMap() {
        return configMap;
    }

    public void setPackageStr(String packageStr) {
        this.packageStr = packageStr;
    }

    public void setClassStr(String classStr) {
        this.classStr = classStr;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public JavaFile generateJavaFile() {
        prepare();
        if (configList == null || configList.size() == 0) return empty();
        return JavaFile.builder(packageStr, typeSpec())
                .addStaticImport(Scheduler.MAINTHREAD)
                .addStaticImport(Scheduler.IO)
                .addStaticImport(Scheduler.NEWTHREAD)
                .addStaticImport(Scheduler.COMPUTATION)
                .addStaticImport(ActivityLifecycle.UNDEFINED)
                .addStaticImport(ActivityLifecycle.CREATE)
                .addStaticImport(ActivityLifecycle.START)
                .addStaticImport(ActivityLifecycle.RESUME)
                .addStaticImport(ActivityLifecycle.PAUSE)
                .addStaticImport(ActivityLifecycle.STOP)
                .addStaticImport(ActivityLifecycle.DESTROY)
                .build();
    }

    private void prepare() {
        if (configMap != null) configList = new ArrayList<>(configMap.values());
    }

    private TypeSpec typeSpec() {
        TypeSpec.Builder builder = TypeSpec.classBuilder(classStr);
        builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(generateBindingMethodSpec());

        builder.superclass(isLifecycleAnnotationContained() ?
                BaseLifecycleUnbinder.class : BaseUnbinder.class);

        return builder.build();
    }

    private boolean isLifecycleAnnotationContained() {
        boolean isContained = false;
        for (NostalgiaConfig config : configList) {
            if (!isLifecycleUndefined(config)) {
                isContained = true;
                break;
            }
        }
        return isContained;
    }

    private MethodSpec generateBindingMethodSpec() {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Object.class, CodeGenerator.Names.METHOD_GENERATEBINDING_PARAM_NAME, Modifier.FINAL)
                .addStatement("super(" + CodeGenerator.Names.METHOD_GENERATEBINDING_PARAM_NAME +")");

        addDetailStatementForGenerateBindingMethodSpec(builder);

        return builder.build();
    }

    private void addDetailStatementForGenerateBindingMethodSpec(MethodSpec.Builder builder) {//descriptor.methodElement.enclosingElement.asType()
        builder.beginControlFlow("if(" + CodeGenerator.Names.METHOD_GENERATEBINDING_PARAM_NAME +".getClass().getCanonicalName().equals(\"" + canonicalName + "\"))");

        for (NostalgiaConfig config : configList) {
            builder.addStatement("bind(" +
                    getNostalgiaStr() +
                    getSubscibeCodeStr(config) +
                    getObserverOnThreadStr(config) +
                    getLifecycleFilterStr(config) +
                    getTakeStr(config) +
                    getSubscribeStr(config) +
                    ")");
        }

        builder.endControlFlow();
    }

    private static String getNostalgiaStr() {
        return Nostalgia.class.getName();
    }

    private static String getSubscibeCodeStr(NostalgiaConfig config) {
        return ".toObservable(\"" + config.getTag() +"\")";
    }

    private static String getObserverOnThreadStr(NostalgiaConfig config) {
        return ".observeOn(" + getNostalgiaStr() + ".internal.resolveSchedulers(" + config.getThread().toString() +"))";
    }

    private static String getTakeStr(NostalgiaConfig config) {
        int times = config.getTakeTimes();
        if (times > 1)
            return ".take(" + times + ")";
        else
            return "";

    }

    private static String getLifecycleFilterStr(NostalgiaConfig config) {
        if (isLifecycleUndefined(config)) return "";
        else return ".filter(lifecycleFilter("+config.getLifecycleFrom()+", "+config.getLifecycleTo()+"))";
    }

    private static boolean isLifecycleUndefined(NostalgiaConfig config) {
        return config.getLifecycleFrom() == ActivityLifecycle.UNDEFINED &&
                config.getLifecycleTo() == ActivityLifecycle.UNDEFINED;
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
                        .addCode(CodeBlock.builder().addStatement("t.printStackTrace()").build().toString())
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

    private static String getReceiveInvokeStr(NostalgiaConfig config) {
        CodeBlock.Builder builder = CodeBlock
                .builder()
                .addStatement("final $T " + CodeGenerator.Names.METHOD_GENERATEBINDING_LOCAL_PARAM_TARGET + "=($T)" + CodeGenerator.Names.METHOD_GENERATEBINDING_PARAM_NAME,
                        config.getElement().getEnclosingElement().asType(),
                        config.getElement().getEnclosingElement().asType()
                );
        if (config.getElement().getParameters() == null || config.getElement().getParameters().size() == 0) {
            builder.beginControlFlow("if(event.getData() instanceof $T)", EmptyContent.class);
            builder.addStatement(CodeGenerator.Names.METHOD_GENERATEBINDING_LOCAL_PARAM_TARGET +"."+config.getElement().getSimpleName()+"()");
        } else {
            TypeMirror typeMirror = config.getElement().getParameters().get(0).asType();
            builder.beginControlFlow("if(event.getData() instanceof " + getType(typeMirror) + ")");
            builder.addStatement(CodeGenerator.Names.METHOD_GENERATEBINDING_LOCAL_PARAM_TARGET + "." + config.getElement().getSimpleName() + "(($T) event.getData())", typeMirror);
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

    private JavaFile empty() {
        return JavaFile.builder(packageStr,
                TypeSpec.classBuilder(classStr)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(BaseUnbinder.class)
                .build()).build();
    }
}
