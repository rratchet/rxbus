/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：library
 *
 * 文件名称：AnnotatedSubscriber.java
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

package com.rratchet.sdk.rxbus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Method;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.functions.ObjectHelper;

/**
 * <pre>
 *
 * 作 者：      ASLai(gdcpljh@126.com).
 * 日 期：      18-8-6
 * 版 本：      V1.0
 * 描 述：      方法带有{@link Subscribe}注解的订阅者
 *
 * </pre>
 *
 * @param <T> the type parameter
 * @author ASLai
 */
class AnnotatedSubscriber<T> extends AbstractSubscriber<DefaultEvent<T>> {

    /**
     * The Hash code.
     */
    private final int hashCode;

    /**
     * The Observer.
     */
    private Object observer;

    /**
     * The AnnotatedMethod.
     */
    private AnnotatedMethod annotatedMethod;

    /**
     * The Filter.
     */
    private Predicate<DefaultEvent<T>> filter;

    /**
     * The Scheduler.
     */
    private Scheduler scheduler;

    /**
     * Instantiates a new Annotated subscriber.
     *
     * @param observer the observer
     * @param method   the method
     */
    AnnotatedSubscriber(@NonNull Object observer, @NonNull Method method) {
        this.observer = observer;
        this.annotatedMethod = AnnotatedMethod.create(observer, method);

        hashCode = 31 * observer.hashCode() + method.hashCode();
    }

    /**
     * Create event subscriber.
     *
     * @param observer the observer
     * @param method   the method
     * @return the event subscriber
     */
    public static AnnotatedSubscriber create(@NonNull Object observer, @NonNull Method method) {
        AnnotatedSubscriber subscriber = new AnnotatedSubscriber<>(observer, method);
        subscriber.withScheduler(subscriber.annotatedMethod.schedulerType.getScheduler());
        return subscriber;
    }

    /**
     * With filter custom subscriber.
     *
     * @param filter the filter
     * @return the custom subscriber
     */
    @SuppressWarnings("WeakerAccess")
    public AnnotatedSubscriber<T> withFilter(@NonNull Predicate<DefaultEvent<T>> filter) {
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
    public AnnotatedSubscriber<T> withScheduler(@NonNull Scheduler scheduler) {
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
    Class<DefaultEvent> getEventClass() {
        return DefaultEvent.class;
    }

    /**
     * Gets annotated method.
     *
     * @return the annotated method
     */
    @NonNull
    public AnnotatedMethod getAnnotatedMethod() {
        return annotatedMethod;
    }

    /**
     * Gets filter.
     *
     * @return the filter
     */
    @Nullable
    Predicate<DefaultEvent<T>> getFilter() {
        return filter == null ? new Predicate<DefaultEvent<T>>() {
            @Override
            public boolean test(DefaultEvent<T> t) throws Exception {
                return true;
            }
        } : filter;
    }

    /**
     * Gets scheduler.
     *
     * @return the scheduler
     */
    @Nullable
    Scheduler getScheduler() {
        return scheduler == null ? AndroidSchedulers.mainThread() : scheduler;
    }

    /**
     * Accept event.
     *
     * @param event the event
     * @throws Exception the exception
     */
    @Override
    protected void acceptEvent(DefaultEvent<T> event) throws Exception {
        annotatedMethod.invoke(event.getData());
    }

    /**
     * Release.
     */
    @Override
    protected void release() {
        observer = null;
        annotatedMethod = null;
        filter = null;
        scheduler = null;
    }

    /**
     * Equals boolean.
     *
     * @param other the other
     * @return the boolean
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        AnnotatedSubscriber<?> that = (AnnotatedSubscriber<?>) other;

        return observer.equals(that.observer) && annotatedMethod.equals(that.annotatedMethod);
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return hashCode;
    }
}
