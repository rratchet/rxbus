/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：rxbus
 *
 * 文件名称：DefaultEvent.java
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-08 10:28:26
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-08 10:28:26
 * 修改备注：
 */

package com.rratchet.support.rxbus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * <pre>
 *
 *      作 者 :        ASLai(laijianhua@ruixiude.com).
 *      日 期 :        2019/4/8
 *      版 本 :        V1.0
 *      描 述 :        description
 *
 *
 * </pre>
 *
 * @author ASLai
 */
class DefaultEvent<T> extends RxEvent<T> {

    public static final String TAG = "DEFAULT";

    public static final String TYPE = "DEFAULT";

    public DefaultEvent() {
        super(TAG, TYPE);
    }

    public DefaultEvent(@NonNull String type) {
        super(TAG, type);
    }

    public DefaultEvent(@NonNull String tag, @NonNull String type) {
        super(tag, type);
    }

    public DefaultEvent(@NonNull String tag, @NonNull String type, @Nullable T data) {
        super(tag, type, data);
    }
}
