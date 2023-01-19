package com.yapbukeji.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yapbukeji.reggie.entities.Dish;
import com.yapbukeji.reggie.entities.SetmealDish;
import com.yapbukeji.reggie.mapper.SetMealDishMapper;
import com.yapbukeji.reggie.service.DishService;
import com.yapbukeji.reggie.service.SetMealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SetMealDishServiceImpl extends ServiceImpl<SetMealDishMapper, SetmealDish> implements SetMealDishService {
    @Autowired
    DishService dishService;

    /**
     * 根据setmealId索引dishId然后查找dish表返回对应list
     *
     * @param setmealId 套餐
     * @return
     */
    @Override
    public List<Dish> getList(Long setmealId) {
        LambdaQueryWrapper<SetmealDish> sDWrapper = new LambdaQueryWrapper<>();
        sDWrapper.eq(SetmealDish::getSetmealId, setmealId);
        List<Dish> dishList = list(sDWrapper).stream().map((item) -> dishService.getById(item.getDishId())).collect(Collectors.toList());
        return dishList;
    }
}
