/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：library
 *
 * 文件名称：BusBridge.java
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import io.reactivex.disposables.CompositeDisposable;

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
class BusBridge<T> {

    /**
     * The Observers.
     */
    public final ConcurrentMap<T, CompositeDisposable> OBSERVERS
            = new ConcurrentHashMap<>();
    /**
     * The Subscribers.
     */
    public final ConcurrentMap<T, CopyOnWriteArraySet<EventSubscriber<?>>> SUBSCRIBERS
            = new ConcurrentHashMap<>();
}
