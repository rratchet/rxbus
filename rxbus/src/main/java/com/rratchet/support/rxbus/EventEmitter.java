/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：rxbus
 *
 * 文件名称：EventEmitter.java
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-08 11:28:09
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-08 11:28:09
 * 修改备注：
 */

package com.rratchet.support.rxbus;

/**
 * <pre>
 *
 *      作 者 :        ASLai(laijianhua@ruixiude.com).
 *      日 期 :        2019/4/8
 *      版 本 :        V1.0
 *      描 述 :        方法事件发射器
 *
 *
 * </pre>
 *
 * @author ASLai
 */
class EventEmitter {

    /**
     * The Tag.
     */
    private String tag = DefaultEvent.TAG;

    /**
     * The Type.
     */
    private String type = DefaultEvent.TYPE;

    /**
     * The Data.
     */
    private Object data;

    public static EventEmitter create() {
        return new EventEmitter();
    }

    /**
     * With tag event emitter.
     *
     * @param tag the tag
     * @return the event emitter
     */
    public EventEmitter withTag(String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * With type event emitter.
     *
     * @param type the type
     * @return the event emitter
     */
    public EventEmitter withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * With data event emitter.
     *
     * @param data the object
     * @return the event emitter
     */
    public EventEmitter withData(Object data) {
        this.data = data;
        return this;
    }

    /**
     * Post.
     */
    public void post() {
        BusProvider.getInstance().post(
                new DefaultEvent<>(tag, type, data)
        );
    }
}
