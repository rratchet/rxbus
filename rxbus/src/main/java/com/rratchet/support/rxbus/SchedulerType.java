/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：rxbus
 *
 * 文件名称：ThreadType.java
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-03 17:15:13
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-03 17:15:13
 * 修改备注：
 */

package com.rratchet.support.rxbus;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *
 *      作 者 :        ASLai(laijianhua@ruixiude.com).
 *      日 期 :        2019/4/3
 *      版 本 :        V1.0
 *      描 述 :        the scheduler type
 *
 *
 * </pre>
 *
 * @author ASLai
 */
public enum SchedulerType {

    /**
     * 直接在当前线程运行, 等同于 {@link SchedulerType#TRAMPOLINE}
     */
    CURRENT_THREAD(Schedulers.trampoline()),

    /**
     * 在主线程中运行，{@link AndroidSchedulers#mainThread()}
     */
    MAIN_THREAD(AndroidSchedulers.mainThread()),

    /**
     * 在单例线程中运行. 更多请查看 {@link Schedulers#single()}
     */
    SINGLE(Schedulers.single()),

    /**
     * 在计算中运行. 更多请查看 {@link Schedulers#computation()}
     */
    COMPUTATION(Schedulers.computation()),

    /**
     *  在I/O线程中运行. 更多请查看 {@link Schedulers#io()}
     */
    IO(Schedulers.io()),

    /**
     * 直接在当前线程上运行. 更多请查看 {@link Schedulers#trampoline()}
     */
    TRAMPOLINE(Schedulers.trampoline()),

    /**
     * 启用一个新线程, 并在新线程上运行. 更多请查看 {@link Schedulers#newThread()}
     */
    NEW_THREAD(Schedulers.newThread()),

    ;

    private Scheduler scheduler;

    SchedulerType(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
