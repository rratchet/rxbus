/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：library
 *
 * 文件名称：RxBusDelegate.java
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-02 20:38:56
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-02 23:18:47
 * 修改备注：
 */

package com.rratchet.sdk.rxbus;

import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.subjects.Subject;

import static com.rratchet.sdk.rxbus.Bus.TAG;

/**
 * <pre>
 *
 *      作 者 :        ASLai(gdcpljh@126.com).
 *      日 期 :        18-10-29
 *      版 本 :        V1.0
 *      描 述 :        description
 *
 *
 * </pre>
 *
 * @author ASLai
 */
final class RxBusDelegate {

    /**
     * The Bus.
     */
    private final Subject<Object> bus;

    /**
     * Instantiates a new Rx bus helper.
     *
     * @param bus the bus
     */
    public RxBusDelegate(Subject<Object> bus) {
        this.bus = bus;
    }

    /**
     * Register.
     *
     * @param bridge   the bridge
     * @param observer the observer
     */
    protected void register(@NonNull BusBridge<Class<?>> bridge, @NonNull Object observer) {
        ObjectHelper.requireNonNull(observer, "Observer to register must not be null.");

        Class<?> observerClass = observer.getClass();

        // 存在观察者时返回
        if (bridge.OBSERVERS.putIfAbsent(observerClass, new CompositeDisposable()) != null) {
            return;
        }

        CompositeDisposable disposable = bridge.OBSERVERS.get(observerClass);

        Set<Class<?>> events = new HashSet<>();

        for (Method method : observerClass.getDeclaredMethods()) {

            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }

            if (!method.isAnnotationPresent(Subscribe.class)) {
                continue;
            }

            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
                throw new IllegalArgumentException("Method " + method.getName() +
                        " has @Subscribe annotation must be public, non-static");
            }

            Class<?>[] params = method.getParameterTypes();

            if (params.length != 1) {
                throw new IllegalArgumentException("Method " + method.getName() +
                        " has @Subscribe annotation must require a single argument");
            }

            Class<?> eventClass = params[0];

            if (eventClass.isInterface()) {
                throw new IllegalArgumentException("RxEvent class must be on a concrete class type.");
            }

            if (!events.add(eventClass)) {
                throw new IllegalArgumentException("Subscriber for " + eventClass.getSimpleName() +
                        " has already been registered.");
            }

            disposable.add(bus.ofType(eventClass)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new AnnotatedSubscriber<>(observer, method)));
        }

    }

    /**
     * Register.
     *
     * @param <T>        the type parameter
     * @param bridge     the bridge
     * @param observer   the observer
     * @param subscriber the subscriber
     */
    protected <T> void register(
            @NonNull BusBridge<Class<?>> bridge,
            @NonNull Object observer,
            @NonNull EventSubscriber<T> subscriber) {

        ObjectHelper.requireNonNull(observer, "Observer to register must not be null.");
        ObjectHelper.requireNonNull(subscriber, "Subscriber to register must not be null.");

        Class<?> observerClass = observer.getClass();

        // 判断当前订阅是否存在，若果不存在则默认创建新的对象
        bridge.SUBSCRIBERS.putIfAbsent(observerClass, new CopyOnWriteArraySet<EventSubscriber<?>>());
        Set<EventSubscriber<?>> subscribers = bridge.SUBSCRIBERS.get(observerClass);

        if (subscribers.contains(subscriber)) {
            // 重复注册
            throw new IllegalArgumentException("Subscriber has already been registered.");
        } else {
            subscribers.add(subscriber);
        }

        Observable<T> observable = bus.ofType(subscriber.getEventClass())
                .observeOn(
                        (subscriber.getScheduler() == null)
                                ? AndroidSchedulers.mainThread()
                                : subscriber.getScheduler()
                );

        // 判断当前观察是否存在，若果不存在则默认创建新的对象
        bridge.OBSERVERS.putIfAbsent(observerClass, new CompositeDisposable());
        CompositeDisposable disposable = bridge.OBSERVERS.get(observerClass);

        // 添加订阅
        disposable.add(
                ((subscriber.getFilter() == null)
                        ? observable
                        : observable.filter(subscriber.getFilter())
                ).subscribe(subscriber)
        );
    }

    /**
     * Register.
     *
     * @param <Event>      the type parameter
     * @param bridge       the bridge
     * @param subjectEvent the subject event
     * @param subscriber   the subscriber
     */
    protected <Event extends RxEvent> void register(
            @NonNull BusBridge<String> bridge,
            @NonNull final Event subjectEvent,
            @NonNull EventSubscriber<Event> subscriber) {

        ObjectHelper.requireNonNull(subscriber, "Subscriber to register must not be null.");

        String eventString = subjectEvent.toEventString();

        // 判断当前订阅是否存在，若果不存在则默认创建新的对象
        bridge.SUBSCRIBERS.putIfAbsent(eventString, new CopyOnWriteArraySet<EventSubscriber<?>>());
        Set<EventSubscriber<?>> subscribers = bridge.SUBSCRIBERS.get(eventString);

        if (subscribers.contains(subscriber)) {
            // 重复注册
            throw new IllegalArgumentException("Subscriber has already been registered.");
        } else {
            subscribers.add(subscriber);
        }

        Observable<Event> observable = bus.ofType(subscriber.getEventClass())
                .observeOn(
                        subscriber.getScheduler() == null
                                ? AndroidSchedulers.mainThread()
                                : subscriber.getScheduler()
                )
                .filter(new Predicate<Event>() {
                    @Override
                    public boolean test(Event event) throws Exception {
                        return subjectEvent.isEqualsSource(event);
                    }
                });

        // 判断当前观察是否存在，若果不存在则默认创建新的对象
        bridge.OBSERVERS.putIfAbsent(eventString, new CompositeDisposable());
        CompositeDisposable disposable = bridge.OBSERVERS.get(eventString);

        // 添加订阅
        disposable.add(
                ((subscriber.getFilter() == null)
                        ? observable
                        : observable.filter(subscriber.getFilter())
                ).subscribe(subscriber)
        );
    }

    /**
     * Unregister.
     *
     * @param bridge   the bridge
     * @param observer the observer
     */
    protected void unregister(@NonNull BusBridge<Class<?>> bridge, @NonNull Object observer) {
        ObjectHelper.requireNonNull(observer, "Observer to unregister must not be null.");

        CompositeDisposable disposable = bridge.OBSERVERS.get(observer.getClass());
        if (disposable == null) {
            Log.d(TAG, "Missing observer, it was registered?");
            return;
        }
        disposable.dispose();
        bridge.OBSERVERS.remove(observer.getClass());

        Set<EventSubscriber<?>> subscribers = bridge.SUBSCRIBERS.get(observer.getClass());
        if (subscribers != null) {
            subscribers.clear();
            bridge.SUBSCRIBERS.remove(observer.getClass());
        }
    }

    /**
     * Unregister.
     *
     * @param bridge the bridge
     * @param event  the subject event
     */
    protected <Event extends RxEvent> void unregister(@NonNull BusBridge<String> bridge, @NonNull Event event) {
        ObjectHelper.requireNonNull(event, "Event to unregister must not be null.");
        unregister(bridge, event.toEventString());
    }

    /**
     * Unregister.
     *
     * @param bridge the bridge
     * @param event  the subject event
     */
    protected void unregister(@NonNull BusBridge<String> bridge, @NonNull String event) {
        ObjectHelper.requireNonNull(event, "Event to unregister must not be null.");

        CompositeDisposable disposable = bridge.OBSERVERS.get(event);
        if (disposable == null) {
            Log.d(TAG, "Missing event, it was registered?");
            return;
        }
        disposable.dispose();
        bridge.OBSERVERS.remove(event);

        Set<EventSubscriber<?>> subscribers = bridge.SUBSCRIBERS.get(event);
        if (subscribers != null) {
            subscribers.clear();
            bridge.SUBSCRIBERS.remove(event);
        }
    }
}
