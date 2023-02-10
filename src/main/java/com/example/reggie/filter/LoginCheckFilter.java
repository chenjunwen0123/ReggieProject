package com.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Res;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 本次请求的路径
        String requestURI = request.getRequestURI();
        //log.info("Intercept URI:{}",requestURI);

        // 不需要拦截的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };

        // 判断当前请求路径是否不需要拦截
        boolean check = false;
        for(String url : urls){
            check = ANT_PATH_MATCHER.match(url,requestURI);
            if(check){
                break;
            }
        }

        // 若不需要拦截，直接放行
        if(check){
            filterChain.doFilter(request,response);
            log.info("Discharge URI:{}",requestURI);
            return;
        }
        // 若需要拦截，但是已登陆状态，放行
        Long sessionId = (Long)request.getSession().getAttribute("employee");
        if(sessionId != null){
            BaseContext.setCurrentId(sessionId);
            filterChain.doFilter(request,response);
            log.info("Discharge URI in Login:{}",requestURI);
            return;
        }

        // 写回信息
        response.getWriter().write(JSON.toJSONString(Res.error("NOTLOGIN")));
        log.info("Intercept URI with Error(NOTLOGIN):{}",requestURI);
    }
}
