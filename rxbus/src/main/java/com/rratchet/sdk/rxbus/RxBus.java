/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：library
 *
 * 文件名称：RxBus.java
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-02 20:38:33
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-02 23:18:47
 * 修改备注：
 */

package com.rratchet.sdk.rxbus;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * <pre>
 *
 * 作 者：      ASLai(gdcpljh@126.com).
 * 日 期：      18-8-6
 * 版 本：      V1.0
 * 描 述：      description
 *
 * </pre>
 *
 * @author ASLai
 */
public final class RxBus implements Bus {

    /**
     * 默认的 {@link BusBridge}
     */
    private final BusBridge<Class<?>> DEFAULT = new BusBridge();

    /**
     * 观察者所订阅事件的 {@link BusBridge}
     */
    private final ConcurrentMap<Class<?>, BusBridge<String>> EVENT_BRIDGE = new ConcurrentHashMap<>();

    /**
     * The Bus.
     */
    private final Subject<Object> bus = PublishSubject.create().toSerialized();

    private final RxBusDelegate delegate;

    public RxBus() {
        this.delegate = new RxBusDelegate(bus);
    }

    @Override
    public void register(@NonNull Object observer) {
        delegate.register(DEFAULT, observer);
    }

    @Override
    public <T> EventSubscriber<T> obtainSubscriber(@NonNull Class<T> eventClass, @NonNull Consumer<T> receiver) {
        return EventSubscriber.create(eventClass, receiver);
    }

    @Override
    public <T> void registerSubscriber(@NonNull Object observer, @NonNull EventSubscriber<T> subscriber) {
        delegate.register(DEFAULT, observer, subscriber);
    }

    @Override
    public <T, Event extends RxEvent<T>> void registerEvent(@NonNull Object observer, @NonNull Event subjectEvent, final @NonNull Consumer<T> receiver) {

        ObjectHelper.requireNonNull(observer, "Observer to register must not be null.");
        ObjectHelper.requireNonNull(subjectEvent, "Event to register must not be null.");
        ObjectHelper.requireNonNull(receiver, "Receiver to register must not be null.");

        Class<Event> eventClass = (Class<Event>) subjectEvent.getClass();
        registerEvent(observer, subjectEvent, EventSubscriber.create(eventClass, new Consumer<Event>() {
                    @Override
                    public void accept(Event event) throws Exception {
                        receiver.accept(event.getData());
                    }
                })
        );
    }

    @Override
    public <Event extends RxEvent> void registerEvent(@NonNull Object observer, @NonNull Event subjectEvent, @NonNull EventSubscriber<Event> subscriber) {

        ObjectHelper.requireNonNull(observer, "Observer to register must not be null.");
        ObjectHelper.requireNonNull(subjectEvent, "Event to register must not be null.");
        ObjectHelper.requireNonNull(subscriber, "Subscriber to register must not be null.");

        Class<?> observerClass = observer.getClass();
        String eventString = subjectEvent.toEventString();

        EVENT_BRIDGE.putIfAbsent(observerClass, new BusBridge<String>());
        BusBridge<String> bridge = EVENT_BRIDGE.get(observerClass);

        delegate.register(bridge, subjectEvent, subscriber);
    }

    @Override
    public void unregister(@NonNull Object observer) {
        // 注销时需要同时注销自定义事件的观察者
        unregisterEvent(observer);

        delegate.unregister(DEFAULT, observer);
    }

    @Override
    public <Event extends RxEvent> void unregisterEvent(@NonNull Object observer, @NonNull Event subjectEvent) {

        ObjectHelper.requireNonNull(observer, "Observer to unregister must not be null.");
        ObjectHelper.requireNonNull(subjectEvent, "Event to unregister must not be null.");

        Class<?> observerClass = observer.getClass();

        EVENT_BRIDGE.putIfAbsent(observerClass, new BusBridge<String>());
        BusBridge<String> bridge = EVENT_BRIDGE.get(observerClass);

        delegate.unregister(bridge, subjectEvent);
    }

    @Override
    public void unregisterEvent(@NonNull Object observer) {

        ObjectHelper.requireNonNull(observer, "Observer to unregister must not be null.");

        Class<?> observerClass = observer.getClass();

        EVENT_BRIDGE.putIfAbsent(observerClass, new BusBridge<String>());
        BusBridge<String> bridge = EVENT_BRIDGE.get(observerClass);

        // 拿出当前观察者所有注册的事件的响应
        ConcurrentMap<String, CompositeDisposable> compositeMap = bridge.OBSERVERS;
        if (compositeMap.isEmpty()) {
            Log.d(TAG, "Missing observer, it was registered?");
            return;
        }

        for (Map.Entry<String, CompositeDisposable> entry : compositeMap.entrySet()) {
            // 遍历所有注册的事件的响应
            CompositeDisposable composite = entry.getValue();
            if (composite == null) {
                Log.d(TAG, "Missing observer, it was registered?");
                continue;
            }
            // 注销当前的响应
            composite.dispose();
        }
        compositeMap.clear();

        // 遍历所有的订阅者并移除
        ConcurrentMap<String, CopyOnWriteArraySet<EventSubscriber<?>>> subscriberMap = bridge.SUBSCRIBERS;
        if (!subscriberMap.isEmpty()) {
            for (Map.Entry<String, CopyOnWriteArraySet<EventSubscriber<?>>> entry : subscriberMap.entrySet()) {
                Set<EventSubscriber<?>> subscribers = entry.getValue();
                if (subscribers != null) {
                    subscribers.clear();
                }
            }
            subscriberMap.clear();
        }
    }

    @Override
    public void post(@NonNull Object event) {
        ObjectHelper.requireNonNull(event, "Event must not be null.");
        bus.onNext(event);
    }
}
