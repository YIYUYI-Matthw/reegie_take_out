package com.yapbukeji.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yapbukeji.reggie.common.ResData;
import com.yapbukeji.reggie.entities.User;
import com.yapbukeji.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/getCode")
    public ResData<String> getCode(HttpServletRequest request, @RequestParam("phoneNumber") String phoneNumber) {
        request.getSession().setAttribute(phoneNumber, "1234");
        return ResData.success("验证信息已发送");
    }

    @PostMapping("/login")
    public ResData<User> login(HttpServletRequest request, @RequestBody Map<String, String> user) {
        // Map<String,String>后就不用toString了
        log.info("请求登录");
        String phoneNumber = user.get("phone");
        String raw_code = request.getSession().getAttribute(phoneNumber).toString();
        String send_code = user.get("code");
        if (raw_code == null)
            return ResData.error("登陆失败");
        // 没有相应信息则添加新用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phoneNumber);
        User user_current = userService.getOne(wrapper);
        if (user_current == null) {
            user_current = new User();
            user_current.setPhone(phoneNumber);
            user_current.setStatus(1);
            userService.save(user_current);
        }
        // 设置session为登录状态
        request.getSession().setAttribute("user", user_current.getId());
        // 返回消息
        if (raw_code.equals(send_code)) {
            log.info("登录成功");
            return ResData.success(user_current);
        } else
            return ResData.error("登陆失败"); // error的泛型是String
    }
}