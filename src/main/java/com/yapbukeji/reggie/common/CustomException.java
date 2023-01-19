package com.yapbukeji.reggie.common;

public class CustomException extends RuntimeException {
    public CustomException(String msg) {
        super(msg);// 更改提示信息
    }
}
