package com.salmonzhg.nostalgia.core;

import com.salmonzhg.nostalgia.core.annotation.Scheduler;

import java.util.Map;
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
    private static INostalgiaGenerator generator;
    private static Map<Integer, Unbinder> bindMap = new ConcurrentHashMap<>();

    public static class internal {
        public static final String GENERATE_PACKAGE_NAME = "com.salmonzhg.nostalgia.generate";
        public static final String GENERATE_CLASS_NAME = "NostalgiaGeneratorImpl";
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

        initializationCheck();

        int hashCode = System.identityHashCode(object);
        Unbinder unbinder;

        if (generator == null) return Unbinder.EMPTY(object);

        unbinder = bindMap.get(hashCode);

        if (unbinder == null || unbinder.isUnbinded()) {
            unbinder = generator.generateBinding(object);
            bindMap.put(hashCode, unbinder);
        }

        return unbinder;
    }

    private static void initializationCheck() {
        try {
            if (generator == null) {
                Class<?> bindingClass = Class.forName(internal.GENERATE_PACKAGE_NAME + "." + internal.GENERATE_CLASS_NAME);
                generator = (INostalgiaGenerator) bindingClass.newInstance();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (internal.mainScheduler == null)
            throw new RuntimeException("have you call Nostalgia.initialize(AndroidSchedulers.mainThread())?");
    }

    static void unbindInternal(Object object) {
        int hashCode = System.identityHashCode(object);
        bindMap.remove(hashCode);
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
