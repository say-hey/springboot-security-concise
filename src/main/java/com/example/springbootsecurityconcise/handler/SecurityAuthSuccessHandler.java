package com.example.springbootsecurityconcise.handler;

import com.example.springbootsecurityconcise.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * security登录认证成功处理器
 */
@Component
public class SecurityAuthSuccessHandler implements AuthenticationSuccessHandler {

    /**
     * 验证成功后执行
     * @param request 请求对象
     * @param response 响应对象
     * @param authentication security验证成功后的封装对象，包括用户的信息
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 登录的用户验证成功后执行
        response.setContentType("text/json;charset=utf-8");
        System.out.println("success handler...");
        Result result = new Result();
        result.setCode(0);
        result.setStatus(200);
        result.setMsg("登录成功");
        // 使用jsckson
        ObjectMapper mapper = new ObjectMapper();
        ServletOutputStream outputStream = response.getOutputStream();
        mapper.writeValue(outputStream, result);


        outputStream.flush();
        outputStream.close();

        // PrintWriter writer = response.getWriter();
        // writer.println("{\"msg\":\"登录成功！\"}");
        // writer.flush();
        // writer.close();
    }
}
