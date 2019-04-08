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
     * 观察者所订阅事件的 {@link BusBridge}
     */
    private final ConcurrentMap<Class<?>, BusBridge<String>> DEFAULT = new ConcurrentHashMap();

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

        ObjectHelper.requireNonNull(observer, "Observer to register must not be null.");

        Class<?> observerClass = observer.getClass();

        DEFAULT.putIfAbsent(observerClass, new BusBridge<String>());
        BusBridge<String> bridge = DEFAULT.get(observerClass);

        delegate.register(bridge, observer);
    }

    @Override
    public <T> void register(@NonNull Object observer, DefaultSubscriber<T> subscriber) {

        ObjectHelper.requireNonNull(observer, "Observer to register must not be null.");
        ObjectHelper.requireNonNull(subscriber, "Subscriber to register must not be null.");

        Class<?> observerClass = observer.getClass();

        DEFAULT.putIfAbsent(observerClass, new BusBridge<String>());
        BusBridge<String> bridge = DEFAULT.get(observerClass);

        delegate.register(bridge, subscriber);
    }

    @Override
    public <T, Event extends RxEvent<T>> void register(@NonNull Object observer, @NonNull Event subjectEvent, final @NonNull Consumer<T> receiver) {

        ObjectHelper.requireNonNull(observer, "Observer to register must not be null.");
        ObjectHelper.requireNonNull(subjectEvent, "Event to register must not be null.");
        ObjectHelper.requireNonNull(receiver, "Receiver to register must not be null.");

        Class<Event> eventClass = (Class<Event>) subjectEvent.getClass();
        register(observer, subjectEvent, DefaultSubscriber.create(eventClass, new Consumer<Event>() {
                    @Override
                    public void accept(Event event) throws Exception {
                        receiver.accept(event.getData());
                    }
                })
        );
    }

    @Override
    public <Event extends RxEvent> void register(@NonNull Object observer, @NonNull Event subjectEvent, @NonNull DefaultSubscriber<Event> subscriber) {

        ObjectHelper.requireNonNull(observer, "Observer to register must not be null.");
        ObjectHelper.requireNonNull(subjectEvent, "Event to register must not be null.");
        ObjectHelper.requireNonNull(subscriber, "Subscriber to register must not be null.");

        Class<?> observerClass = observer.getClass();
        String eventString = subjectEvent.toEventString();

        DEFAULT.putIfAbsent(observerClass, new BusBridge<String>());
        BusBridge<String> bridge = DEFAULT.get(observerClass);

        delegate.register(bridge, subjectEvent, subscriber);
    }

    @Override
    public <Event extends RxEvent> void unregisterEvent(@NonNull Object observer, @NonNull Event subjectEvent) {

        ObjectHelper.requireNonNull(observer, "Observer to unregister must not be null.");
        ObjectHelper.requireNonNull(subjectEvent, "Event to unregister must not be null.");

        Class<?> observerClass = observer.getClass();

        DEFAULT.putIfAbsent(observerClass, new BusBridge<String>());
        BusBridge<String> bridge = DEFAULT.get(observerClass);

        delegate.unregister(bridge, subjectEvent);
    }

    @Override
    public void unregisterEvent(@NonNull Object observer) {

        ObjectHelper.requireNonNull(observer, "Observer to unregister must not be null.");

        Class<?> observerClass = observer.getClass();

        DEFAULT.putIfAbsent(observerClass, new BusBridge<String>());
        BusBridge<String> bridge = DEFAULT.get(observerClass);

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
        ConcurrentMap<String, CopyOnWriteArraySet<DefaultSubscriber<?>>> subscriberMap = bridge.SUBSCRIBERS;
        if (!subscriberMap.isEmpty()) {
            for (Map.Entry<String, CopyOnWriteArraySet<DefaultSubscriber<?>>> entry : subscriberMap.entrySet()) {
                Set<DefaultSubscriber<?>> subscribers = entry.getValue();
                if (subscribers != null) {
                    subscribers.clear();
                }
            }
            subscriberMap.clear();
        }
    }

    @Override
    public void unregister(@NonNull Object observer) {

        // 注销时需要同时注销自定义事件的观察者
        unregisterEvent(observer);

        Class<?> observerClass = observer.getClass();

        DEFAULT.putIfAbsent(observerClass, new BusBridge<String>());
        BusBridge<String> bridge = DEFAULT.get(observerClass);

        delegate.unregister(bridge, observer);
    }

    @Override
    public void post(@NonNull Object data) {

        ObjectHelper.requireNonNull(data, "Post data must not be null.");
        if (data instanceof RxEvent) {
            bus.onNext(data);
        } else {
            EventEmitter.create()
                    .withData(data)
                    .post();
        }
    }

    @Override
    public void post(String type, Object data) {
        EventEmitter.create()
                .withType(type)
                .withData(data)
                .post();
    }

    @Override
    public void post(String tag, String type, Object data) {
        EventEmitter.create()
                .withTag(tag)
                .withType(type)
                .withData(data)
                .post();
    }
}
