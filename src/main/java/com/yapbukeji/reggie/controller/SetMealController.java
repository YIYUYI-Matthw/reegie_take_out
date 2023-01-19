package com.yapbukeji.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yapbukeji.reggie.common.ResData;
import com.yapbukeji.reggie.entities.Dish;
import com.yapbukeji.reggie.entities.Setmeal;
import com.yapbukeji.reggie.dto.SetmealDto;
import com.yapbukeji.reggie.entities.SetmealDish;
import com.yapbukeji.reggie.service.CategoryService;
import com.yapbukeji.reggie.service.SetMealDishService;
import com.yapbukeji.reggie.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    @Autowired
    SetMealService setMealService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    SetMealDishService setMealDishService;

    @GetMapping("/page")
    public ResData<Page<SetmealDto>> getSetMealList(Integer page, Integer pageSize, String name) {
        log.info("获取套餐信息");
        // 根据page、pageSize、name来查询套餐
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Setmeal::getName, name);
        setMealService.page(pageInfo, wrapper);
        // 建立smDto并部分拷贝查询page
        Page<SetmealDto> pageDtoInfo = new Page<>();
        BeanUtils.copyProperties(pageInfo, pageDtoInfo, "records");
        // 设置records
        List<SetmealDto> setmealDtoList = pageInfo.getRecords().stream().map((item) -> {
            // 部分拷贝
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            // 分类信息
            setmealDto.setCategoryName(categoryService.getNameByCId(item.getCategoryId()));
            return setmealDto;
        }).collect(Collectors.toList());
        pageDtoInfo.setRecords(setmealDtoList);
        return ResData.success(pageDtoInfo);
    }

    /**
     * 新增套餐
     *
     * @param setmealDto json转对象
     * @return 返回新增结果
     */
    @PostMapping
    public ResData<String> addSetMeal(@RequestBody SetmealDto setmealDto) {
        log.info("新增菜品");
        // 把setmeal存进去
        setMealService.save(setmealDto);
        // setmeal的id是公共字段生成的，直接获取即可
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes().stream().peek((item) -> {
            item.setSetmealId(setmealDto.getId());
        }).collect(Collectors.toList());
        setMealDishService.saveBatch(setmealDishList);
        return ResData.success("新增成功");
    }

    /**
     * 根据setmeal的id把setmeal及其相关dish进行删除
     *
     * @param setmealId 字符串接收：处理删除和批量删除
     * @return 删除结果
     */
    @DeleteMapping
    public ResData<String> deleteSetmeal(@RequestParam("ids") String setmealId) {
        List<Long> ids = Arrays.stream(setmealId.split(",")).map(Long::parseLong).collect(Collectors.toList());
        for (Long id : ids)
            deleteSetmeals(id);
        return ResData.success("删除成功");
    }

    private void deleteSetmeals(Long setmealId) {
        log.info("删除套餐");
        // 查询setmealId相关setmeal_dish
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId, setmealId);
        setMealDishService.remove(wrapper);
        // 删除套餐项
        setMealService.removeById(setmealId);
    }

    /**
     * 请求的是套餐：返回套餐而不是套餐里面的具体菜品
     * 不是json格式的话直接使用实体类就能转
     *
     * @param categoryId
     * @param status
     * @return
     */
    @GetMapping("/list")
    public ResData<List<Setmeal>> getList(@RequestParam("categoryId") Long categoryId, Integer status) {
        LambdaQueryWrapper<Setmeal> setMealWrapper = new LambdaQueryWrapper<>();
        setMealWrapper.eq(Setmeal::getCategoryId, categoryId);
        List<Setmeal> setmealList = setMealService.list(setMealWrapper);
        if (setmealList == null)
            return ResData.success(null);
        return ResData.success(setmealList);
    }
}
