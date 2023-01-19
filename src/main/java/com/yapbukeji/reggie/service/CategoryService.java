package com.yapbukeji.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yapbukeji.reggie.entities.Category;

public interface CategoryService extends IService<Category> {
    // 写一个自己的方法：删除分类的时候顺带判断菜品，然后决定是否删除
    public void remove(Long id);

    // 根据cId获取name
    String getNameByCId(Long id);
}