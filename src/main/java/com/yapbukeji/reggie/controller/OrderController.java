package com.yapbukeji.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.yapbukeji.reggie.common.BaseContext;
import com.yapbukeji.reggie.common.ResData;
import com.yapbukeji.reggie.entities.Orders;
import com.yapbukeji.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.cglib.core.Local;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/submit")
    public ResData<String> pay(@RequestBody Orders orders) {
        log.info("创建订单");
        orderService.submit(orders);
        return ResData.success("下单成功");
    }

    @GetMapping("/page")
    public ResData<Page<Orders>> getOrderPage(@RequestParam("page") Integer page, @RequestParam("pageSize") Integer pageSize,
                                              String number, String beginTime, String endTime) {
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> orderWrapper = new LambdaQueryWrapper<>();
        // 订单号
        orderWrapper.like(number != null, Orders::getNumber, number);
        // 订单时间
        if (beginTime != null && endTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime beginTimeF = LocalDateTime.parse(beginTime, formatter);
            LocalDateTime endTimeF = LocalDateTime.parse(endTime, formatter);
            orderWrapper.between(Orders::getOrderTime, beginTimeF, endTimeF);// 好像直接比string也能达到效果
        }
        // 查询
        orderService.page(pageInfo, orderWrapper);
        return ResData.success(pageInfo);
    }
}