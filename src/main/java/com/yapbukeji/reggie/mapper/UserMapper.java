package com.yapbukeji.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yapbukeji.reggie.entities.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
