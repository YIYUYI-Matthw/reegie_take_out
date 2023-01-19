package com.yapbukeji.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yapbukeji.reggie.entities.OrderDetail;
import com.yapbukeji.reggie.mapper.OrderDetailMapper;
import com.yapbukeji.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
