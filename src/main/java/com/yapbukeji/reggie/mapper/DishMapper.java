package com.yapbukeji.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yapbukeji.reggie.entities.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
