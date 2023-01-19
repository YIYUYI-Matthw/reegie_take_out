package com.yapbukeji.reggie.dto;


import com.yapbukeji.reggie.entities.Setmeal;
import com.yapbukeji.reggie.entities.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
