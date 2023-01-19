package com.yapbukeji.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yapbukeji.reggie.common.ResData;
import com.yapbukeji.reggie.entities.Employee;
import com.yapbukeji.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController // RC = Controller + RB
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request：把id存到session中
     * @param employee：传递json数据封装为对象，注意：json数据的key要和被封装对象的属性一致
     * @return RestData<T>：操作结果
     */
    @PostMapping("/login")
    public ResData<Employee> login(HttpServletRequest request, @RequestBody Employee employee) { // 传递json：需要用到RB注解
        log.info("员工登录controller");
        // 1. 密码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes()); // spring提供这个工具类
        // 2. 用户名查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();// 条件【查询】：定义查询规则，传入MP方法
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee employee_instance = employeeService.getOne(queryWrapper);
        // 3. 查询员工状态：账号是否可用
        if (employee_instance == null)
            return ResData.error("用户不存在");
        if (!employee_instance.getPassword().equals(password))
            return ResData.error("用户名或密码错误");
        if (employee_instance.getStatus() == 0)
            return ResData.error("当前用户不可用");
        log.info("登陆成功");
        // 4. 更新session：返回数据时，会一并返回session
        request.getSession().setAttribute("employee", employee_instance.getId()); // 注意这里的id是数据库中的
        // 5. 返回结果（session include）
        return ResData.success(employee_instance); // 因为有RestController-RB：返回内容会被添加到HTTP的body中
    }

    /**
     * 登出当前用户
     *
     * @param request 获取session并清除
     * @return 登出状态
     */
    @PostMapping("/logout") // 因为前端请求不是logout/id，所以这里也不用
    public ResData<String> logout(HttpServletRequest request) {
        log.info("登出controller");
        // 1. 清除session
        request.getSession().removeAttribute("employee");
        // 2. logout
        return ResData.success("已退出");
    }

    /**
     * 新增员工到数据库
     *
     * @param employee 新增的员工信息
     * @return 返回ResData<String>类型数据：仅需要告知是否添加成功，不需要其他响应数据
     */
    @PostMapping // 路由为/employee的post请求对应新增员工
    public ResData<String> addEmployee(@RequestBody Employee employee) {
        log.info("新增员工controller");
        // 设置未传递属性：公共属性由OH设定
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes())); // 初始密码
        // insert
        boolean saved = employeeService.save(employee);
        if (saved)
            return ResData.success("添加成功");
        else
            return ResData.error("添加失败，检查信息正确性");

        /* 异常处理方法一
        try {
            boolean saved = employeeService.save(employee);
            if (saved)
                return ResData.success("添加成功");
            else
                return ResData.error("添加失败，检查信息正确性");
        } catch (Exception e) {
            return ResData.error("添加失败，检查信息正确性");
        }
         */
    }

    /**
     * 员工分页查询
     *
     * @param page     哪一页
     * @param pageSize 一页最大size
     * @param name     模糊查询内容
     * @return 查询结果
     */
    @SuppressWarnings("all")
    @GetMapping("/page")
    public ResData<Page> getPageList(@RequestParam("page") Integer page,
                                     @RequestParam("pageSize") Integer pageSize,
                                     String name) { // 这个name可能不会传过来，所以就emm不屑RP了
        log.info("参数：page = {}, pageSize = {}, name = {}", page, pageSize, name);
        // 分页构造器：page
        Page pageInfo = new Page(page, pageSize);
        // 条件构造器：wrapper
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Employee::getName, name); // boolean：true时添加where
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 查询
        employeeService.page(pageInfo, queryWrapper);
        return ResData.success(pageInfo); // 返回页面数据信息
        /* 不使用RequestParam注解
        log.info(request.getQueryString()); // page=1&pageSize=10
        Map<String, String[]> requestMap = request.getParameterMap();
         */
    }

    /**
     * 更新员工信息，包括：“编辑”和“状态”
     *
     * @param employee json格式数据接收后转对象
     * @return 更新结果
     */
    @PutMapping // 注意这里不要加"/"
    public ResData<String> update(@RequestBody Employee employee) {
        // 只有管理员才能更改：在前端已经做了调整：只有管理员才有“禁用”选项
        // 1. 创建对象：status已经在前端更改过了：更新信息：在OH上更新
        // 2. 更改权限：js只能处理16位，而Long为19位，这里增加了对象转换器
        employeeService.updateById(employee);
        return ResData.success("修改成功");
    }


    /**
     * 单独获取一个员工的信息：然后编辑
     *
     * @param id 员工id：转换器转Long
     * @return 员工信息
     */
    @GetMapping("/{id}")
    public ResData<Employee> getUser(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null)
            return ResData.success(employee);
        else
            return ResData.error("该员工不存在");
    }
}