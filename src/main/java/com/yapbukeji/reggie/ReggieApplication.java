package com.yapbukeji.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j // lombok提供的注解：getter、setter等注解简化
@SpringBootApplication
@ServletComponentScan // 扫描webfilter注解，加载过滤器
@EnableTransactionManagement // 支持事务管理
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功"); // lombok提供的：输出日志
    }
}