package com.yapbukeji.reggie.common;

/**
 * 基于ThreadLoca封装工具类，用户id报错和获取，泛型为Long-id
 */
public class BaseContext extends ThreadLocal<Long> {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
