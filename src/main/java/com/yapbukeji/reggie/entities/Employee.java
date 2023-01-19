package com.yapbukeji.reggie.entities;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data // lombok的注解：省去getter、setter、toString
public class Employee {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String username; // unique
    private String password;
    private String phone;
    private String sex;
    private String idNumber;
    // 和表中的id_number不一样：在yml中开启了驼峰命名（MP的功能），所以可以这么写
    private Integer status;

    // 在公共字段上添加@TableFiled注解
    @TableField(fill = FieldFill.INSERT) // 插入时填充这个字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入或更新时填充这个字段
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}