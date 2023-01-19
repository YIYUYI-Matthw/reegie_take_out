package com.yapbukeji.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yapbukeji.reggie.entities.Dish;
import com.yapbukeji.reggie.entities.SetmealDish;

import java.util.List;

public interface SetMealDishService extends IService<SetmealDish> {
    List<Dish> getList(Long setmealId);
}
