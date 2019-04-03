/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：library
 *
 * 文件名称：RxEvent.java
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
public class RxEvent<T> {

    /**
     * The Tag.
     */
    private final String tag;
    /**
     * The Type.
     */
    private final String type;
    /**
     * The Data.
     */
    private T data;

    /**
     * 实例化一个事件对象.
     */
    public RxEvent() {
        tag = getClass().getSimpleName();
        type = getClass().getSimpleName();
    }

    /**
     * 实例化一个事件对象.
     *
     * @param type 事件类型
     */
    public RxEvent(@NonNull String type) {
        this.tag = getClass().getSimpleName();
        this.type = type;
    }

    /**
     * 实例化一个事件对象.
     *
     * @param tag  事件标签
     * @param type 事件类型
     */
    public RxEvent(@NonNull String tag, @NonNull String type) {
        this.tag = tag;
        this.type = type;
    }

    /**
     * 实例化一个事件对象.
     *
     * @param tag  事件标签
     * @param type 事件类型
     * @param data 事件数据
     */
    public RxEvent(@NonNull String tag, @NonNull String type, @Nullable T data) {
        this.tag = tag;
        this.type = type;
        this.data = data;
    }

    /**
     * 获取事件标签
     *
     * @return the tag
     */
    public String getTag() {
        return tag;
    }


    /**
     * 获取事件类型
     *
     * @return the type
     */
    public String getType() {
        return type;
    }


    /**
     * 获取事件数据
     *
     * @return the data
     */
    public T getData() {
        return data;
    }

    /**
     * 设置事件数据
     *
     * @param data the data
     *
     * @return the data
     */
    public RxEvent<T> setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 返回同一事件源
     *
     * @param event the rx type
     *
     * @return boolean boolean
     */
    public boolean isEqualsSource(RxEvent event) {
        if (event != null && getClass().getName().equals(event.getClass().getName())) {
            return (getTag() != null && getTag().equals(event.getTag()))
                    && (getType() != null && getType().equals(event.getType()));
        }
        return false;
    }

    /**
     * 事件字符串
     *
     * @return the string
     */
    public String toEventString() {
        return getClass().getCanonicalName() + "@" + tag + "@" + type;
    }
}
