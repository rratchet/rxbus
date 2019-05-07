/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：library
 *
 * 文件名称：Subscribe.java
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
 * @author ASLai
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    /**
     * 事件的标签，默认为 {@link DefaultEvent#TAG}
     *
     * @return
     */
    String tag() default DefaultEvent.TAG;

    /**
     * 事件的类型，默认为 {@link DefaultEvent#TYPE}
     *
     * @return
     */
    String type() default DefaultEvent.TYPE;

    /**
     * 线程调度类型，默认为当前线程
     *
     * @return
     */
    SchedulerType schedulerType() default SchedulerType.CURRENT_THREAD;

}
