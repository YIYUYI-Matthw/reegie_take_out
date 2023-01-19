package com.yapbukeji.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yapbukeji.reggie.common.BaseContext;
import com.yapbukeji.reggie.common.ResData;
import com.yapbukeji.reggie.entities.ShoppingCart;
import com.yapbukeji.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public ResData<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("添加到购物车");
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> shoppingWrapper = new LambdaQueryWrapper<>();
        shoppingWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        shoppingWrapper.and((wrapper) -> {
            wrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                    .or().eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        });
        ShoppingCart shoppingCartDB = shoppingCartService.getOne(shoppingWrapper);
        if (shoppingCartDB != null) {
            shoppingCartDB.setNumber(shoppingCartDB.getNumber() + 1);
            shoppingCartService.updateById(shoppingCartDB);
            return ResData.success(shoppingCartDB);
        }
        shoppingCart.setNumber(1);
        shoppingCartService.save(shoppingCart);
        return ResData.success(shoppingCart);
    }

    @PostMapping("/sub")
    public ResData<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        log.info("减去一个");
        shoppingCart.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> shoppingWrapper = new LambdaQueryWrapper<>();
        shoppingWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());
        shoppingWrapper.and((wrapper) -> {
            wrapper.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                    .or().eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        });
        ShoppingCart shoppingCartDB = shoppingCartService.getOne(shoppingWrapper);
        if (shoppingCartDB != null) {
            if (shoppingCartDB.getNumber() > 1) {
                shoppingCartDB.setNumber(shoppingCartDB.getNumber() - 1);
                shoppingCartService.updateById(shoppingCartDB);
                return ResData.success(shoppingCartDB);
            } else {
                shoppingCartService.removeById(shoppingCartDB.getId());
                shoppingCartDB.setNumber(0);
                return ResData.success(shoppingCartDB);
            }
        }
        return ResData.success(null);
    }

    @GetMapping("/list")
    public ResData<List<ShoppingCart>> getShopping() {
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(wrapper);
        return ResData.success(shoppingCartList);
    }

    @DeleteMapping("/clean")
    public ResData<String> clearAll() {
        log.info("清空购物车");
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        LambdaQueryWrapper<ShoppingCart> shoppingWrapper = new LambdaQueryWrapper<>();
        shoppingWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(shoppingWrapper);
        return ResData.success("清空完毕");
    }
}