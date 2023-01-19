package com.yapbukeji.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.yapbukeji.reggie.common.BaseContext;
import com.yapbukeji.reggie.common.ResData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录：过滤器
 * Filter是javax.servlet定义的类：这里用servlet的过滤器来做校验
 */
@Slf4j
@WebFilter(filterName = "LoginCheck", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher(); // spring工具类：路径匹配

    /**
     * 路径匹配，检查当前路径是否可以直接放行
     *
     * @param urls：放行路径
     * @param requestUri：请求路径
     * @return boolean：放行-true
     */
    public boolean checkPass(String[] urls, String requestUri) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestUri))
                return true;
        }
        return false;
    }


    /**
     * 过滤器：检查所有uri，拦截部分非法访问（未登录访问后续内容）请求
     *
     * @param servletRequest  请求对象
     * @param servletResponse 响应对象
     * @param filterChain     过滤器链：chain.doFilter(req, res)：放行
     * @throws IOException      异常
     * @throws ServletException 异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("路径拦截Filter");
        // 定义放行路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/getCode", // 这个是手机登录请求验证码
                "/user/login",
        };
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 1. 获取请求url
        String requestUri = request.getRequestURI();
        log.info("请求路径：{}", request.getRequestURI());
        // 2. 判定是否需要限制登陆
        if (checkPass(urls, requestUri)) {
            log.info("非限制路径");
            filterChain.doFilter(request, response); // 放行
            return;
        }
        // 3-1. 【后台管理】获取登录状态：session中尝试获取用户
        if (request.getSession().getAttribute("employee") != null) {
            log.info("登录状态验证通过");
            // 3.5 存储当前用户id备用：填充公共字段
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
            // 放行
            filterChain.doFilter(request, response); // 放行
            return;
        }

        // 3-2. 【客户端管理】获取登录状态：session中尝试获取用户
        Long userId = (Long) request.getSession().getAttribute("user");
        if (userId != null) {
            log.info("登录状态验证通过");
            // 存储用户id：在后续添加地址等需要使用
            BaseContext.setCurrentId(userId);
            // 放行
            filterChain.doFilter(request, response); // 放行
            return;
        }

        // 4. 截停：前端设置拦截器：当resData.code=0且resData.msg="未登录"时，跳转login页面，所以这里直接通过response.writer写回去即可
        response.getWriter().write(JSON.toJSONString(ResData.error("NOTLOGIN"))); // 这个写法好经典啊：过滤器用的是http不是spring
    }
}