package com.yapbukeji.reggie.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果类，服务端响应数据封装为此对象
 *
 * @param <T>
 */
@SuppressWarnings("all")
@Data // 这是干嘛用的？加了之后，406错误没了
public class ResData<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data; // T：泛型，根据传入的类型决定
    private Map map = new HashMap<>(); // 动态数据：待续

    public static <T> ResData<T> success(T object) {
        ResData<T> resData = new ResData<T>();
        resData.data = object;
        resData.code = 1;
        resData.msg = "操作成功";
        return resData;
    }

    public static <T> ResData<T> error(String msg) {
        ResData<T> resData = new ResData<T>();
        resData.data = null;
        resData.code = 0;
        resData.msg = msg;
        return resData;
    }

    public ResData<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
