package com.yapbukeji.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yapbukeji.reggie.common.BaseContext;
import com.yapbukeji.reggie.common.CustomException;
import com.yapbukeji.reggie.common.ResData;
import com.yapbukeji.reggie.entities.*;
import com.yapbukeji.reggie.mapper.OrderMapper;
import com.yapbukeji.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Autowired
    ShoppingCartService shoppingCartService;
    @Autowired
    UserService userService;
    @Autowired
    AddressBookService addressBookService;
    @Autowired
    OrderDetailService orderDetailService;

    @Transactional
    public void submit(Orders orders) {
        // 用户id
        Long userId = BaseContext.getCurrentId();
        // 用户、地址
        User user = userService.getById(userId);
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        // 购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingWrapper = new LambdaQueryWrapper<>();
        shoppingWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> cartList = shoppingCartService.list(shoppingWrapper);
        // TODO：这里有个订单总额写的假数据
        AtomicInteger amount = new AtomicInteger(200);// 原子操作：多线程也没问题
        // 订单号
        Long orderId = IdWorker.getId();

        // 异常处理
        if (cartList == null || user == null || addressBook == null)
            throw new CustomException("状态异常，无法下单");

        // 插入数据：订单表
        orders.setAmount(BigDecimal.valueOf(amount.get()));
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setOrderTime(LocalDateTime.now()); // 下单时间
        orders.setCheckoutTime(LocalDateTime.now()); // checkout时间
        orders.setStatus(2);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(user.getPhone());
        orders.setAddress(addressBook.getDetail()); // 随便写下吧
        save(orders);

        // 插入数据：明细表
        List<OrderDetail> orderDetailList = cartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setName(item.getName());
            orderDetail.setAmount(item.getAmount());
            return orderDetail;
        }).collect(Collectors.toList());
        orderDetailService.saveBatch(orderDetailList);
        // 清空购物车数据
        LambdaQueryWrapper<ShoppingCart> cartWrapper = new LambdaQueryWrapper<>();
        cartWrapper.eq(ShoppingCart::getUserId, userId);
        shoppingCartService.remove(cartWrapper);
    }
}
