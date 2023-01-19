package com.yapbukeji.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yapbukeji.reggie.entities.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
