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
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        log.info("contextPath:{}, servletPath:{}", contextPath,servletPath);
        String requestURI = request.getRequestURI();
        //log.info("Intercept URI:{}",requestURI);

        // 不需要拦截的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        // 判断当前请求路径是否在不需要拦截的路径列表中
        boolean check = checkWhiteList(requestURI,urls);

        // 若不需要拦截，直接放行
        if(check){
            filterChain.doFilter(request,response);
            log.info("Discharge URI:{}",requestURI);
            return;
        }

        Long sessionId = (Long)request.getSession().getAttribute("employee");
        log.info("SessionId:{}",sessionId);
        Long userSessionId = (Long)request.getSession().getAttribute("userSession");
        log.info("userSessionId:{}",userSessionId);

        if(userSessionId == null && sessionId == null) {
            responseNotLogin(requestURI, response);
            return;
        }
        // 若需要拦截
        // 后台是否处于已登陆状态，若是，则放行
        // front，用户是否已登陆
        if(sessionId != null){
            BaseContext.setCurrentId(sessionId);
            filterChain.doFilter(request, response);
            log.info("[Backend]Discharge URI in login:{}", requestURI);
            return;
        }
        if(userSessionId != null){
            BaseContext.setCurrentId(userSessionId);
            filterChain.doFilter(request, response);
            log.info("[front]Discharge URI in Login:{}", requestURI);
        }
    }
    private boolean checkWhiteList(String requestURI, String[] urls){
        boolean check = false;
        for(String url : urls){
            check = ANT_PATH_MATCHER.match(url,requestURI);
            if(check){
                break;
            }
        }
        return check;
    }
    private void responseNotLogin(String requestURI,HttpServletResponse response) throws IOException {
        response.getWriter().write(JSON.toJSONString(Res.error("NOTLOGIN")));
        log.info("Intercept URI with Error(NOTLOGIN):{}",requestURI);
    }
}
