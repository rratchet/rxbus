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

package com.rratchet.support.rxbus;

import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.subjects.Subject;

import static com.rratchet.support.rxbus.Bus.TAG;

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
     * 实例化RxBus的代理类
     *
     * @param bus the bus
     */
    RxBusDelegate(Subject<Object> bus) {
        this.bus = bus;
    }

    /**
     * Register.
     *
     * @param bridge   the bridge
     * @param observer the observer
     */
    void register(@NonNull BusBridge<String> bridge, @NonNull Object observer) {

        ObjectHelper.requireNonNull(observer, "Observer to register must not be null.");

        Class<?> observerClass = observer.getClass();
        String observerName = observerClass.getCanonicalName();

        // 存在观察者时返回，目前设计是一个类只能订阅一次
        if (bridge.OBSERVERS.putIfAbsent(observerName, new CompositeDisposable()) != null) {
            return;
        }

        CompositeDisposable disposable = bridge.OBSERVERS.get(observerName);

        for (Method method : observerClass.getDeclaredMethods()) {


            //  TODO 开始过滤方法

            if (method.isBridge() || method.isSynthetic()) {
                continue;
            }

            if (!method.isAnnotationPresent(Subscribe.class)) {
                continue;
            }

            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
                throw new IllegalArgumentException("Method " + method.getName()
                        + " has @Subscribe annotation must be public, non-static");
            }

            // 获取根据注解生成的订阅器
            AnnotatedSubscriber subscriber = AnnotatedSubscriber.create(observer, method);

            final DefaultEvent subjectEvent = subscriber.getAnnotatedMethod().defaultEvent;

            Observable observable = bus.ofType(subscriber.getEventClass())
                    .filter(new Predicate<DefaultEvent>() {
                        @Override
                        public boolean test(DefaultEvent event) throws Exception {
                            return subjectEvent.isEqualsSource(event);
                        }
                    })
                    .filter(subscriber.getFilter())
                    .observeOn(subscriber.getScheduler());

            // 添加订阅
            disposable.add(
                    observable.subscribe(subscriber)
            );
        }


    }

    /**
     * Register.
     *
     * @param <T>        the type parameter
     * @param bridge     the bridge
     * @param subscriber the subscriber
     */
    <T> void register(
            @NonNull BusBridge<String> bridge,
            @NonNull final DefaultSubscriber<T> subscriber) {

        ObjectHelper.requireNonNull(subscriber, "Subscriber to register must not be null.");

        final Class<?> eventClass = subscriber.getEventClass();
        // 使用事件类作为观察者的名称
        String observerName = eventClass.getCanonicalName();
        // 使用订阅者哈希码作为订阅者名称
        String subscriberName = String.valueOf(subscriber.hashCode());

        // 判断当前订阅是否存在，若果不存在则默认创建新的对象
        bridge.SUBSCRIBERS.putIfAbsent(subscriberName, new CopyOnWriteArraySet<DefaultSubscriber<?>>());
        Set<DefaultSubscriber<?>> subscribers = bridge.SUBSCRIBERS.get(subscriberName);

        if (subscribers.contains(subscriber)) {
            // 重复注册
            throw new IllegalArgumentException("Subscriber has already been registered.");
        } else {
            subscribers.add(subscriber);
        }

        Observable<DefaultEvent> observable = bus.ofType(DefaultEvent.class)
                .filter(new Predicate<DefaultEvent>() {
                    @Override
                    public boolean test(DefaultEvent defaultEvent) throws Exception {
                        Object data = defaultEvent.getData();
                        if (eventClass == data) {
                            return true;
                        }
                        try {
                            return subscriber.getFilter().test((T) data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                })
                .observeOn(subscriber.getScheduler());

        // 判断当前观察是否存在，若果不存在则默认创建新的对象
        bridge.OBSERVERS.putIfAbsent(observerName, new CompositeDisposable());
        CompositeDisposable disposable = bridge.OBSERVERS.get(observerName);

        // 添加订阅
        disposable.add(
                observable.subscribe(new Consumer<DefaultEvent>() {
                    @Override
                    public void accept(DefaultEvent defaultEvent) throws Exception {
                        Object data = defaultEvent.getData();
                        if (eventClass == data) {
                            subscriber.acceptEvent(null);
                        } else {
                            subscriber.acceptEvent((T) data);
                        }
                    }
                })
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
    <Event extends RxEvent> void register(
            @NonNull BusBridge<String> bridge,
            @NonNull final Event subjectEvent,
            @NonNull DefaultSubscriber<Event> subscriber) {

        ObjectHelper.requireNonNull(subscriber, "Subscriber to register must not be null.");

        // 使用事件类作为观察者的名称
        String observerName = subjectEvent.getClass().getCanonicalName();
        // 使用事件值作为订阅者名称
        String subscriberName = subjectEvent.toEventString();

        // 判断当前订阅是否存在，若果不存在则默认创建新的对象
        bridge.SUBSCRIBERS.putIfAbsent(subscriberName, new CopyOnWriteArraySet<DefaultSubscriber<?>>());
        Set<DefaultSubscriber<?>> subscribers = bridge.SUBSCRIBERS.get(subscriberName);

        if (subscribers.contains(subscriber)) {
            // 重复注册
            throw new IllegalArgumentException("Subscriber has already been registered.");
        } else {
            subscribers.add(subscriber);
        }

        Observable<Event> observable = bus.ofType(subscriber.getEventClass())
                .filter(new Predicate<Event>() {
                    @Override
                    public boolean test(Event event) throws Exception {
                        return subjectEvent.isEqualsSource(event);
                    }
                })
                .filter(subscriber.getFilter())
                .observeOn(subscriber.getScheduler());

        // 判断当前观察是否存在，若果不存在则默认创建新的对象
        bridge.OBSERVERS.putIfAbsent(observerName, new CompositeDisposable());
        CompositeDisposable disposable = bridge.OBSERVERS.get(observerName);

        // 添加订阅
        disposable.add(
                observable.subscribe(subscriber)
        );
    }

    /**
     * Unregister.
     *
     * @param bridge   the bridge
     * @param observer the observer
     */
    void unregister(@NonNull BusBridge<String> bridge, @NonNull Object observer) {
        ObjectHelper.requireNonNull(observer, "Observer to unregister must not be null.");

        Class<?> observerClass = observer.getClass();
        String observerName = observerClass.getCanonicalName();

        CompositeDisposable disposable = bridge.OBSERVERS.get(observerName);
        if (disposable == null) {
            Log.d(TAG, "Missing observer, it was registered?");
            return;
        }
        disposable.dispose();
        bridge.OBSERVERS.remove(observerName);

        Set<DefaultSubscriber<?>> subscribers = bridge.SUBSCRIBERS.get(observerName);
        if (subscribers != null) {
            subscribers.clear();
            bridge.SUBSCRIBERS.remove(observerName);
        }
    }

    /**
     * Unregister.
     *
     * @param <Event> the type parameter
     * @param bridge  the bridge
     * @param event   the subject event
     */
    <Event extends RxEvent> void unregister(@NonNull BusBridge<String> bridge, @NonNull Event event) {
        ObjectHelper.requireNonNull(event, "Event to unregister must not be null.");
        unregister(bridge, event.toEventString());
    }

    /**
     * Unregister.
     *
     * @param bridge the bridge
     * @param event  the subject event
     */
    void unregister(@NonNull BusBridge<String> bridge, @NonNull String event) {
        ObjectHelper.requireNonNull(event, "Event to unregister must not be null.");

        CompositeDisposable disposable = bridge.OBSERVERS.get(event);
        if (disposable == null) {
            Log.d(TAG, "Missing event, it was registered?");
            return;
        }
        disposable.dispose();
        bridge.OBSERVERS.remove(event);

        Set<DefaultSubscriber<?>> subscribers = bridge.SUBSCRIBERS.get(event);
        if (subscribers != null) {
            subscribers.clear();
            bridge.SUBSCRIBERS.remove(event);
        }
    }
}
