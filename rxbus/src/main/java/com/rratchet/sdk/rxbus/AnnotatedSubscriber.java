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

import java.lang.reflect.Method;

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
 *
 * @author ASLai
 */
class AnnotatedSubscriber<T> extends AbstractSubscriber<T> {

    /**
     * The Hash code.
     */
    private final int hashCode;

    /**
     * The Observer.
     */
    private Object observer;

    /**
     * The Method.
     */
    private Method method;

    /**
     * Instantiates a new Annotated subscriber.
     *
     * @param observer the observer
     * @param method   the method
     */
    public AnnotatedSubscriber(@NonNull Object observer, @NonNull Method method) {
        this.observer = observer;
        this.method = method;

        hashCode = 31 * observer.hashCode() + method.hashCode();
    }

    @Override
    protected void acceptEvent(T event) throws Exception {
        method.invoke(observer, event);
    }

    @Override
    protected void release() {
        observer = null;
        method = null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        AnnotatedSubscriber<?> that = (AnnotatedSubscriber<?>) other;

        return observer.equals(that.observer) && method.equals(that.method);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }
}
