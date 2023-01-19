package com.yapbukeji.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yapbukeji.reggie.dto.DishDto;
import com.yapbukeji.reggie.entities.Dish;
import com.yapbukeji.reggie.entities.DishFlavor;

import java.util.List;

public interface DishService extends IService<Dish> {
    // 新增菜品同时操作两张表
    void saveWithFlavor(DishDto dishDto);
    // 更新菜品和xx
    void updateWithFlavor(DishDto dishDto);

    // 根据id（dishId）查询flavor_list
    List<DishFlavor> getFlavorByDId(Long dishId);
}
