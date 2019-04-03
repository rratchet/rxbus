/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：library
 *
 * 文件名称：BusProvider.java
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
public class BusProvider {
    /**
     * Instantiates a new Bus provider.
     */
    private BusProvider() {
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static Bus getInstance() {
        return BusHolder.INSTANCE;
    }

    /**
     * The type Bus holder.
     */
    private static final class BusHolder {
        /**
         * The constant INSTANCE.
         */
        final static Bus INSTANCE = new RxBus();
    }
}
