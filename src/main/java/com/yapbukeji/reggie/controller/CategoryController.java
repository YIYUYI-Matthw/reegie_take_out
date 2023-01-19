package com.yapbukeji.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yapbukeji.reggie.common.ResData;
import com.yapbukeji.reggie.entities.Category;
import com.yapbukeji.reggie.entities.Employee;
import com.yapbukeji.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController // Controller + RB
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService; // 本来是autowired，但是这里尝试使用构造器注入

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 请求分类list数据：/category/page?page=1&pageSize=10 【GET】
     *
     * @param pageNum  第几页
     * @param pageSize 每页size
     * @return 查询结果
     */
//    @SuppressWarnings("all")
    @GetMapping("/page")
    public ResData<Page> getPage(@RequestParam("page") Integer pageNum,
                                 @RequestParam("pageSize") Integer pageSize) {
        log.info("请求菜品分类信息");
        // 分页构造器：分页依据、当页内容
        Page pageInfo = new Page(pageNum, pageSize);
        // 条件构造器：不用，因为没有name，所以不用where
        // 查询
        categoryService.page(pageInfo);
        return ResData.success(pageInfo); // 这里就不根据是否为null返回error了：因为确实可能是null
    }

    /**
     * 添加菜品：/category  【POST】 {"name": "yoyo", "type": "1", "sort": "10"}
     * post：json-使用RB注解转换为POJO
     * 不用request，因为公共字段有处理
     *
     * @param category 接收数据映射为分类
     * @return 添加结果
     */
    @PostMapping
    public ResData<String> addCategory(@RequestBody Category category) {
        log.info("添加菜品分类");
        categoryService.save(category);
        return ResData.success("添加成功");
    }


    /**
     * 修改菜品：/category  【PUT】 {"name": "yoyo", "type": "1", "sort": "10"}
     * post：json-使用RB注解转换为POJO
     * 不用request，因为公共字段有处理
     *
     * @param category 接收数据映射为分类
     * @return 添加结果
     */
    @PutMapping
    public ResData<String> updateCategory(@RequestBody Category category) {
        log.info("修改分类数据");
        categoryService.updateById(category);
        return ResData.success("修改成功");
    }

    /**
     * 删除某个分类，如果该分类下有菜品则不能删除
     *
     * @param id 菜品分类id
     * @return 删除结果，不能删除时抛出自定义异常
     */
    @DeleteMapping()
    public ResData<String> deleteCategory(@RequestParam("ids") Long id) {
        log.info("删除分类");
        // 查看改分类是否关联菜品，如果关联则不能删除
        categoryService.remove(id);
        // TODO：之后的删除应该是update：更新isDeleted即可
        return ResData.success("删除分类");
    }

    /**
     * 获取菜品分类数据
     *
     * @param type type为1时表示菜品分类，0表示套餐分类，null时不分类全部获取
     * @return
     */
    @GetMapping("/list")
    public ResData<List<Category>> getCategoriesList(Integer type) {
        log.info("获取category列表");
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(type != null, Category::getType, type);
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(wrapper);
        return ResData.success(categoryList);
    }
}