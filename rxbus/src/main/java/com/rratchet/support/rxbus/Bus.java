/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：library
 *
 * 文件名称：Bus.java
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-02 18:12:46
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-02 23:18:47
 * 修改备注：
 */

package com.rratchet.support.rxbus;

import android.support.annotation.NonNull;

import io.reactivex.functions.Consumer;

/**
 * <pre>
 *
 * 作 者：      ASLai(gdcpljh@126.com).
 * 日 期：      18-8-6
 * 版 本：      V1.0
 * 描 述：
 *
 * 注销观察者方法的等级：
 *  1、{@link Bus#unregister(Object)}
 *  2、{@link Bus#unregisterEvent(Object)}
 *  3、{@link Bus#unregisterEvent(Object, RxEvent)}
 *
 * </pre>
 *
 * @author ASLai
 */
public interface Bus {

    /**
     * The constant TAG.
     */
    String TAG = Bus.class.getSimpleName();

    /**
     * 注册观察者.
     *
     * @param observer the observer
     */
    void register(@NonNull Object observer);

    /**
     * 注册观察者并订阅相应的事件.
     *
     * @param observer   观察者对象
     * @param subscriber 订阅者对象
     * @param <T>
     */
    <T> void register(@NonNull Object observer, DefaultSubscriber<T> subscriber);

    /**
     * 注册观察者并订阅当前事件
     *
     * @param observer
     * @param subjectEvent
     * @param receiver
     * @param <Event>
     */
    <T, Event extends RxEvent<T>> void register(@NonNull Object observer, @NonNull Event subjectEvent, @NonNull Consumer<T> receiver);

    /**
     * 注册观察者并订阅当前事件
     *
     * @param observer
     * @param subjectEvent
     * @param subscriber
     * @param <Event>
     */
    <Event extends RxEvent> void register(@NonNull Object observer, @NonNull Event subjectEvent, @NonNull DefaultSubscriber<Event> subscriber);

    /**
     * 注销观察者.
     *
     * @param observer the observer
     */
    void unregister(@NonNull Object observer);

    /**
     * 注销观察者当前注册的自定义事件.
     *
     * @param <Event>      the type parameter
     * @param observer     the observer
     * @param subjectEvent the subject event
     */
    <Event extends RxEvent> void unregisterEvent(@NonNull Object observer, @NonNull Event subjectEvent);

    /**
     * 注销观察者所有注册的自定义事件.
     *
     * @param observer the observer
     */
    void unregisterEvent(@NonNull Object observer);

    /**
     * 发布.
     *
     * @param event the event
     */
    void post(@NonNull Object event);

    /**
     * 发布.
     *
     * @param type the type
     * @param data the data
     */
    void post(String type, Object data);

    /**
     * 发布.
     *
     * @param tag  the tag
     * @param type the type
     * @param data the data
     */
    void post(String tag, String type, Object data);


}
