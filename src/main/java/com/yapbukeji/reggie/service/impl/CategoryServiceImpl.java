package com.yapbukeji.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yapbukeji.reggie.common.CustomException;
import com.yapbukeji.reggie.entities.Category;
import com.yapbukeji.reggie.entities.Dish;
import com.yapbukeji.reggie.entities.Setmeal;
import com.yapbukeji.reggie.mapper.CategoryMapper;
import com.yapbukeji.reggie.service.CategoryService;
import com.yapbukeji.reggie.service.DishService;
import com.yapbukeji.reggie.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    DishService dishService;
    @Autowired
    SetMealService setMealService;

    @Override
    public void remove(Long id) {
        // 查询当前分类是否关联菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int dishNum = dishService.count(dishLambdaQueryWrapper);
        if (dishNum > 0) {
            // 当前分类下有其他菜品
            throw new CustomException("当前分类下有其他菜品");
        }
        // 查询套餐关联
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int setNum = setMealService.count(setmealLambdaQueryWrapper);
        if (setNum > 0) {
            // 当前分类下有其他菜品：抛出业务异常
            throw new CustomException("当前分类下有其他套餐");
        }
        // 啥都没有：删除
        super.removeById(id);
    }

    @Override
    public String getNameByCId(Long id) {
        Category category = getById(id);
        return category.getName();
    }
}