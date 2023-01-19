package com.yapbukeji.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yapbukeji.reggie.entities.Setmeal;
import com.yapbukeji.reggie.mapper.SetmealMapper;
import com.yapbukeji.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SetMealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetMealService {
}
