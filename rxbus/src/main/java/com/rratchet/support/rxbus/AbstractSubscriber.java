/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：library
 *
 * 文件名称：AbstractSubscriber.java
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

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

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
 * @param <T> the type parameter
 *
 * @author ASLai
 */
abstract class AbstractSubscriber<T> implements Consumer<T>, Disposable {

    /**
     * The Disposed.
     */
    private volatile boolean disposed;

    @Override
    public void accept(T event) {
        try {
            acceptEvent(event);
        } catch (Exception e) {
            throw new RuntimeException("Could not dispatch event: " + event.getClass(), e);
        }
    }

    @Override
    public void dispose() {
        if (!disposed) {
            disposed = true;
            release();
        }
    }

    @Override
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * 接受事件
     *
     * @param event the event
     *
     * @throws Exception the exception
     */
    protected abstract void acceptEvent(T event) throws Exception;

    /**
     * 释放
     */
    protected abstract void release();

}
