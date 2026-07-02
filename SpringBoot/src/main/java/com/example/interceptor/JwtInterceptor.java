package com.example.interceptor;

import com.example.constant.BaseContext;
import com.example.constant.Constant;
import com.example.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.remove();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        //判断是否不为动态方法
        if (!(handler instanceof HandlerMethod))
            return true;
        //获取token
        String token = request.getHeader("Authorization");
        //验证token是否为null或者空
        if (token == null ||token.trim().isEmpty()) {
            response.setStatus(401);
            return false;
        }
        //如果Bearer开头清除Bearer
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        //验证token并且获取当前登录的id
        try {
            Claims claims = jwtUtil.getClaims(token);
            Integer id = Integer.valueOf(claims.getSubject());
            BaseContext.set(id);
            log.info("当前用户为id：{}", id);
            return true;
        }catch (Exception e){
            response.setStatus(401);
            return false;
        }
    }
}
