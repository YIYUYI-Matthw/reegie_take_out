package com.yapbukeji.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yapbukeji.reggie.dto.DishDto;
import com.yapbukeji.reggie.entities.Dish;
import com.yapbukeji.reggie.entities.DishFlavor;
import com.yapbukeji.reggie.mapper.DishMapper;
import com.yapbukeji.reggie.service.DishFlavorService;
import com.yapbukeji.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    DishFlavorService dishFlavorService;

    @Override
    public void saveWithFlavor(DishDto dishDto) {
        save(dishDto); // dishDto继承Dish，所以直接保存
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        for (DishFlavor dishFlavor : dishFlavorList) {
            dishFlavor.setDishId(dishDto.getId()); // dishDto继承Dish，getId就是dishId
        }
        dishFlavorService.saveBatch(dishFlavorList); // saveBatch：批量保存
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        updateById(dishDto);
        // 有些口味是新增的，没有id，得插入而不是xx
        List<DishFlavor> dishFlavorList = dishDto.getFlavors();
        for (DishFlavor flavor : dishFlavorList) {
            if (flavor.getId() == null) {
                flavor.setDishId(dishDto.getId());
                dishFlavorService.save(flavor);
            } else {
                dishFlavorService.updateById(flavor);
            }
        }
    }

    @Override
    public List<DishFlavor> getFlavorByDId(Long dishId) {
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dishId);
        return dishFlavorService.list(wrapper);
    }
}
