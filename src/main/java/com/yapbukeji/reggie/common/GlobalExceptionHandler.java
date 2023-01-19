package com.yapbukeji.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理：通过AOP，专门针对controller类做拦截增强：如果报错就xxx
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class}) // 所有controller类都会被拦截
@ResponseBody // 返回数据转json
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 异常处理：SQL数据库数据更新异常
     *
     * @return 响应异常信息
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class) // 处理异常的类型
    public ResData<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.info("捕捉到异常，处理后返回");
        String exceptionMessage = exception.getMessage();
        log.error(exceptionMessage);
        if (exceptionMessage.contains("Duplicate entry"))
            return ResData.error("当前用户已存在");
        return ResData.error("操作异常，请稍后重试");
    }

    /**
     * 处理自定义异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(CustomException.class) // 处理异常的类型
    public ResData<String> selfExceptionHandler(CustomException exception) {
        log.info("捕捉到异常，处理后返回");
        String exceptionMessage = exception.getMessage();
        log.error(exceptionMessage);
        return ResData.error(exception.getMessage());
    }
}