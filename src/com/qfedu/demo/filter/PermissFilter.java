package com.qfedu.demo.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class PermissFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        //获取请求地址
        String requestURI = request.getRequestURI();
        if (requestURI.contains("login") || requestURI.contains("easyui") || requestURI.contains("h-ui")) {
            //登录或者静态资源相关，直接放行
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            //检查用户是否登录
            Object loginUser = request.getSession().getAttribute("loginUser");
            if (loginUser == null) {
                //说明用户没有登录，跳转到登录页面
                resp.sendRedirect(request.getContextPath() + "/login");
            }else{
                //说明用户已经登录了
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
