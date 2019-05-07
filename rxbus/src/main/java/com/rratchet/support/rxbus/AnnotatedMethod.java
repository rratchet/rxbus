/*
 * Copyright (c) 2019. RRatChet.
 * Created by ASLai(laijianhua@rratchet.com).
 *
 * 项目名称：rratchet-sdk-rxbus-trunk
 * 模块名称：rxbus
 *
 * 文件名称：AnnotatedMethod.java
 * 文件描述：
 *
 * 创 建 人：ASLai(laijianhua@rratchet.com)
 *
 * 上次修改时间：2019-04-08 14:44:16
 *
 * 修 改 人：ASLai(laijianhua@rratchet.com)
 * 修改时间：2019-04-08 14:44:16
 * 修改备注：
 */

package com.rratchet.support.rxbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.reactivex.disposables.Disposable;

/**
 * <pre>
 *
 *      作 者 :        ASLai(laijianhua@ruixiude.com).
 *      日 期 :        2019/4/8
 *      版 本 :        V1.0
 *      描 述 :        带有{@link Subscribe}注解的方法
 *
 *
 * </pre>
 *
 * @author ASLai
 */
class AnnotatedMethod {

    public final Method        method;
    public final Object        observer;
    public final DefaultEvent  defaultEvent;
    public final SchedulerType schedulerType;
    public final Class         dataType;

    private Disposable disposable;

    private AnnotatedMethod(Object observer, Method method) {
        this.method = method;
        this.observer = observer;

        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length == 0) {
            //无参
            dataType = Void.class;
        } else if (parameterTypes.length == 1) {
            Class dateType = parameterTypes[0];
            if (dateType == int.class) {
                dataType = Integer.class;
            } else if (dateType == long.class) {
                dataType = Long.class;
            } else if (dateType == double.class) {
                dataType = Double.class;
            } else if (dateType == float.class) {
                dataType = Float.class;
            } else if (dateType == boolean.class) {
                dataType = Boolean.class;
            } else {
                dataType = dateType;
            }
        } else {
            throw new IllegalArgumentException("Method " + method.getName() +
                    " has @Subscribe annotation must require a single argument or none");
        }
        if (dataType.isInterface()) {
            throw new IllegalArgumentException("The subscribe param class must be on a concrete class type.");
        }
        Subscribe subscribe = method.getAnnotation(Subscribe.class);
        schedulerType = subscribe.schedulerType();

        String tag = subscribe.tag();
        String type = subscribe.type();

        defaultEvent = new DefaultEvent(tag, type);

    }

    public static AnnotatedMethod create(Object observer, Method method) {
        return new AnnotatedMethod(observer, method);
    }

    public Class<?> getEventClass() {
        return DefaultEvent.class;
    }

    public void invoke(Object data) {
        try {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes == null || parameterTypes.length == 0) {
                method.invoke(observer);
            } else if (parameterTypes.length == 1) {
                method.invoke(observer, data);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        AnnotatedMethod that = (AnnotatedMethod) other;
        return observer.equals(that.observer) && method.equals(that.method);
    }

    public void setDisposable(Disposable disposable) {
        this.disposable = disposable;
    }

    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
