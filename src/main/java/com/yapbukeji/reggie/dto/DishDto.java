package com.yapbukeji.reggie.dto;

import com.yapbukeji.reggie.entities.Dish;
import com.yapbukeji.reggie.entities.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
