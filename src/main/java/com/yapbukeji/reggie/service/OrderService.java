package com.yapbukeji.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yapbukeji.reggie.entities.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
