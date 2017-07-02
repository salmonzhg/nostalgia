package com.salmonzhg.nostalgia.core;

import com.salmonzhg.nostalgia.core.annotation.Scheduler;
import com.salmonzhg.nostalgia.core.lifecycleadapter.LifecycleAdapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * author: Salmon
 * date: 2017-06-28 15:47
 * github: https://github.com/billy96322
 * email: salmonzhg@foxmail.com
 */

public class Nostalgia {
    private static Subject<Event> mBus = PublishSubject.create();
    private static Map<Integer, Unbinder> bindMap = new ConcurrentHashMap<>();

    public static class internal {
        public static final String GENERATE_CLASS_NAME_POSTFIX = "_NostalgiaBinding";
        public static io.reactivex.Scheduler mainScheduler;

        public static io.reactivex.Scheduler resolveSchedulers(Scheduler nostalgiaScheduler) {
            io.reactivex.Scheduler scheduler;
            switch (nostalgiaScheduler) {
                case MAINTHREAD:
                    scheduler = mainScheduler;
                    break;
                case IO:
                    scheduler = Schedulers.io();
                    break;
                case NEWTHREAD:
                    scheduler = Schedulers.newThread();
                    break;
                case COMPUTATION:
                    scheduler = Schedulers.computation();
                    break;
                default:
                    scheduler = mainScheduler;
                    break;
            }
            return scheduler;
        }

        public static void removeBinding(Object object) {
            int hashCode = System.identityHashCode(object);
            bindMap.remove(hashCode);
        }
    }

    public static void initialize(io.reactivex.Scheduler mainThreadScheduler) {
        internal.mainScheduler = mainThreadScheduler;
    }

    public static void post(String tag) {
        mBus.onNext(new Event(tag));
    }

    public static void post(String tag, Object content) {
        mBus.onNext(new Event(tag, content));
    }

    public static Unbinder bind(Object object) {
        return bind(object, null);
    }

    public static Unbinder bind(Object object, LifecycleAdapter adapter) {
        initializationCheck();

        int hashCode = System.identityHashCode(object);
        Unbinder unbinder;

        unbinder = bindMap.get(hashCode);

        if (unbinder == null || unbinder.isUnbound()) {
            unbinder = generateBinding(object);

            if (unbinder instanceof BaseLifecycleUnbinder && adapter != null) {
                ((BaseLifecycleUnbinder)unbinder).setLifecycleAdapter(adapter);
            }

            bindMap.put(hashCode, unbinder);
        }

        return unbinder;
    }

    private static Unbinder generateBinding(Object targetObject) {
        try {
            Class<?> targetClass = targetObject.getClass();
            Constructor<? extends Unbinder> constructor = findBindingConstructorForClass(targetClass);

            if (constructor == null) return Unbinder.EMPTY;

            return (Unbinder) constructor.newInstance(targetObject);

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return Unbinder.EMPTY;
    }

    private static void initializationCheck() {
        if (internal.mainScheduler == null)
            throw new RuntimeException("have you call Nostalgia.initialize(AndroidSchedulers.mainThread())?");
    }

    private static Constructor<? extends Unbinder> findBindingConstructorForClass(Class<?> cls) {
        Constructor<? extends Unbinder> bindingCtor;
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            return null;
        }
        try {
            Class<?> bindingClass = Class.forName(clsName + internal.GENERATE_CLASS_NAME_POSTFIX);
            bindingCtor = (Constructor<? extends Unbinder>) bindingClass.getConstructor(Object.class);
        } catch (ClassNotFoundException e) {
            bindingCtor = findBindingConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        return bindingCtor;
    }

    public static Observable<Event> toObservable(final String tag) {
        return mBus.hide().filter(new Predicate<Event>() {
            @Override
            public boolean test(@NonNull Event event) throws Exception {
                return tag.equals(event.getTag());
            }
        });
    }


}
