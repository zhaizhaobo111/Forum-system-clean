package com.example.demo.interceptor;

import com.example.demo.common.AppCpnfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.Location;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/*
* 登录拦截器
* */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Value("${one-forum.login.url}")
    private String defaultURL;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    /**
     * 前置处理(对请求进行预处理)
     * @return true: 继续流程 <br/> false :流程中断
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取session 对象
        HttpSession session = request.getSession(false);
        //判断session是否有效
        if(session!=null&&session.getAttribute(AppCpnfig.USER_SESSION)!=null){
            //用户为已登陆状态，校验通过
            return true;
        }
//        //检验url是否正确
//        if(!defaultURL.startsWith("/")){
//            defaultURL="/"+defaultURL;
//        }
//        //校验不通过，跳转到登陆页面
//      response.sendRedirect(defaultURL);
//        //中断流程
//        return false;


        String requestURI = request.getRequestURI();
        if (requestURI.contains("/article/create")) {
            // 发帖接口：未登录时强制返回 JSON
            response.setContentType("application/json;charset=utf-8");
            response.setStatus(200);
            PrintWriter out = response.getWriter();

            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", "请先登录");
            result.put("data", null);

            out.write(OBJECT_MAPPER.writeValueAsString(result));
            out.flush();
            out.close();
        } else {
            // 其他接口：仍然重定向到登录页
            if (!defaultURL.startsWith("/")) {
                defaultURL = "/" + defaultURL;
            }
            response.sendRedirect(defaultURL);
        }

        // 中断流程
        return false;
    }
}
