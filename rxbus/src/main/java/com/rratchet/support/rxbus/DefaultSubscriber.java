/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：library
 *
 * 文件名称：DefaultSubscriber.java
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

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * <pre>
 *
 * 作 者：      ASLai(gdcpljh@126.com).
 * 日 期：      18-8-6
 * 版 本：      V1.0
 * 描 述：      默认的订阅者
 *
 * </pre>
 *
 * @param <T> the type parameter
 * @author ASLai
 */
public class DefaultSubscriber<T> extends AbstractSubscriber<T> {

    /**
     * The Hash code.
     */
    private final int hashCode;

    /**
     * The RxEvent class.
     */
    private Class<T>     eventClass;
    /**
     * The Receiver.
     */
    private Consumer<T>  receiver;
    /**
     * The Filter.
     */
    private Predicate<T> filter;
    /**
     * The Scheduler.
     */
    private Scheduler    scheduler;

    /**
     * Instantiates a new Custom subscriber.
     *
     * @param eventClass the event class
     * @param receiver   the receiver
     */
    DefaultSubscriber(@NonNull Class<T> eventClass, @NonNull Consumer<T> receiver) {
        this.eventClass = eventClass;
        this.receiver = receiver;

        hashCode = receiver.hashCode();
    }

    /**
     * Create event subscriber.
     *
     * @param <T>        the type parameter
     * @param eventClass the event class
     * @param receiver   the receiver
     * @return the event subscriber
     */
    public static <T> DefaultSubscriber<T> create(@NonNull Class<T> eventClass, @NonNull Consumer<T> receiver) {

        ObjectHelper.requireNonNull(eventClass, "RxEvent class must not be null.");
        if (eventClass.isInterface()) {
            throw new IllegalArgumentException("RxEvent class must be on a concrete class type.");
        }
        ObjectHelper.requireNonNull(receiver, "Receiver must not be null.");
        return new DefaultSubscriber<>(eventClass, receiver);
    }

    /**
     * With filter custom subscriber.
     *
     * @param filter the filter
     * @return the custom subscriber
     */
    @SuppressWarnings("WeakerAccess")
    public DefaultSubscriber<T> withFilter(@NonNull Predicate<T> filter) {
        ObjectHelper.requireNonNull(filter, "Filter must not be null.");
        this.filter = filter;
        return this;
    }

    /**
     * With scheduler custom subscriber.
     *
     * @param scheduler the scheduler
     * @return the custom subscriber
     */
    @SuppressWarnings("WeakerAccess")
    public DefaultSubscriber<T> withScheduler(@NonNull Scheduler scheduler) {
        ObjectHelper.requireNonNull(scheduler, "Scheduler must not be null.");
        this.scheduler = scheduler;
        return this;
    }

    /**
     * Gets event class.
     *
     * @return the event class
     */
    @NonNull
    Class<T> getEventClass() {
        return eventClass;
    }

    /**
     * Gets filter.
     *
     * @return the filter
     */
    @NonNull
    Predicate<T> getFilter() {
        return filter == null ? new Predicate<T>() {
            @Override
            public boolean test(T t) throws Exception {
                return true;
            }
        } : filter;
    }

    /**
     * Gets scheduler.
     *
     * @return the scheduler
     */
    @NonNull
    Scheduler getScheduler() {
        return scheduler == null ? AndroidSchedulers.mainThread() : scheduler;
    }

    @Override
    protected void acceptEvent(T event) throws Exception {
        receiver.accept(event);
    }

    @Override
    protected void release() {
        eventClass = null;
        receiver = null;
        filter = null;
        scheduler = null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        DefaultSubscriber<?> that = (DefaultSubscriber<?>) other;

        return receiver.equals(that.receiver);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
