package com.yapbukeji.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yapbukeji.reggie.common.ResData;
import com.yapbukeji.reggie.dto.DishDto;
import com.yapbukeji.reggie.entities.Category;
import com.yapbukeji.reggie.entities.Dish;
import com.yapbukeji.reggie.entities.DishFlavor;
import com.yapbukeji.reggie.service.CategoryService;
import com.yapbukeji.reggie.service.DishFlavorService;
import com.yapbukeji.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    DishService dishService;
    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 添加菜品
     * 添加时根据口味和菜品其他属性分别操作两张表
     *
     * @param dishDto 自建实体类，继承dish同时接收flavor列表
     * @return 返回添加结果
     */
    @PostMapping

    public ResData<String> addDish(@RequestBody DishDto dishDto) {
        // TODO：如果菜名应该做一定区分
        log.info("新增菜品");
        String redisKey = "" + dishDto.getCategoryId() + "1";
        dishService.saveWithFlavor(dishDto);
        // 有新增，把改菜品所属类别对应的redis缓存进行清楚
        Object obj = redisTemplate.opsForValue().get(redisKey);
        if (obj != null) {
            log.info("菜品更新，清理缓存");
            redisTemplate.delete(redisKey);
        }
        return ResData.success("新增成功");
    }

    /**
     * 查询当前菜品，要增加categoryName字段
     *
     * @param page     第几页
     * @param pageSize 一页的条目数量
     * @param name     查询内容
     * @return PageDto对象：包括Dish的属性+categoryName
     */
    @GetMapping("/page")
    public ResData<Page<DishDto>> getList(Integer page, Integer pageSize, String name) {
        // 查询name对应的所有dish并分页
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Dish::getName, name);
        wrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo, wrapper);
        // 建立dishDto的page对象，准备一个categoryName属性
        Page<DishDto> pageDtoInfo = new Page<>(page, pageSize);
        // 通过BU拷贝page Dish内容到page Dto：除了records之外的其他查询特征
        BeanUtils.copyProperties(pageInfo, pageDtoInfo, "records"); // 除了records，其实属性都进行拷贝
        // lambda表达式根据每个id来查询并获得dishDto列表
        List<DishDto> dishDtos = pageInfo.getRecords().stream().map((item) -> {
            Long id = item.getCategoryId(); // 分类id
            Category category = categoryService.getById(id); // 分类对象
            DishDto dishDto = new DishDto();
            // BU拷贝Dish对象内容到DishDto
            BeanUtils.copyProperties(item, dishDto); // source target
            if (category != null)
                dishDto.setCategoryName(category.getName());
            return dishDto;
        }).collect(Collectors.toList()); // 收集xx
        // page Dto设置records
        pageDtoInfo.setRecords(dishDtos);
        return ResData.success(pageDtoInfo);
    }

    /**
     * 修改菜品时通过dishId获得其他信息
     *
     * @param id dishId
     * @return 查询结果：DishDto
     */
    @GetMapping("/{id}")
    public ResData<DishDto> getDishDto(@PathVariable Long id) {
        // 获取dish信息
        Dish dish = dishService.getById(id); // 不能向下转型
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        // 设置查询wrapper
        LambdaQueryWrapper<Category> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Category::getId, dishDto.getCategoryId());
        // 获取category
        dishDto.setCategoryName(categoryService.getNameByCId(dish.getCategoryId()));
        // 获取flavor
        dishDto.setFlavors(dishService.getFlavorByDId(id));
        // 返回结果
        return ResData.success(dishDto);
    }

    /**
     * 更新菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public ResData<String> updateDish(@RequestBody DishDto dishDto) {
        log.info("更新菜品");
        String redisKey = "" + dishDto.getCategoryId() + "1";
        // 先执行更新操作
        dishService.updateWithFlavor(dishDto);
        // 有更新，把改菜品所属类别对应的redis缓存进行清楚
        Object obj = redisTemplate.opsForValue().get(redisKey);
        if (obj != null) {
            log.info("菜品更新，清理缓存");
            redisTemplate.delete(redisKey);
        }
        return ResData.success("更新成功");
    }

    /**
     * 根据类别把类别对应的菜品全部返回
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public ResData<List<DishDto>> getDishList(Long categoryId, Integer status) {
        // TODO：客户端请求时会发送status=1，即正在出售中
        // redis-key
        String redis_key = "" + categoryId + status;
        // 返回菜品
        List<DishDto> dishDtoList = null;
        // 尝试读取缓存
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(redis_key);
        if (dishDtoList != null) {
            log.info("缓存中有");
            return ResData.success(dishDtoList);
        }

        // 缓存没有，查询并存储
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId, categoryId);
        dishDtoList = dishService.list(wrapper).stream().map((item) -> {
            Long id = item.getCategoryId(); // 分类id
            Category category = categoryService.getById(id); // 分类对象
            DishDto dishDto = new DishDto();
            // BU拷贝Dish对象内容到DishDto
            BeanUtils.copyProperties(item, dishDto); // source target
            if (category != null) {
                dishDto.setCategoryName(category.getName());
                // 设置flavors
                LambdaQueryWrapper<DishFlavor> dishFWrapper = new LambdaQueryWrapper<>();
                dishFWrapper.eq(DishFlavor::getDishId, item.getId());
                dishDto.setFlavors(dishFlavorService.list(dishFWrapper));
            }
            return dishDto;
        }).collect(Collectors.toList());
        // 缓存至redis：有效期1h
        redisTemplate.opsForValue().set(redis_key, dishDtoList, 1, TimeUnit.HOURS);
        return ResData.success(dishDtoList);
    }

    // TODO：status=0的修改
}