/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：app
 *
 * 文件名称：DefaultEvent.java
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-03 11:33:15
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-03 11:33:15
 * 修改备注：
 */

package com.rratchet.support.rxbus.demo.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.rratchet.support.rxbus.RxEvent;

/**
 * <pre>
 *
 *      作 者 :        ASLai(laijianhua@ruixiude.com).
 *      日 期 :        2019/4/3
 *      版 本 :        V1.0
 *      描 述 :        description
 *
 *
 * </pre>
 *
 * @author ASLai
 */
public class DefaultEvent extends RxEvent<EventInfo> {

    public DefaultEvent() {
        super();
    }

    public DefaultEvent(@NonNull String type) {
        super(type);
    }

    public DefaultEvent(@NonNull String tag, @NonNull String type) {
        super(tag, type);
    }

    public DefaultEvent(@NonNull String tag, @NonNull String type, @Nullable EventInfo data) {
        super(tag, type, data);
    }
}
