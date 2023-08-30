package com.example.springbootsecurityconcise.handler;

import com.example.springbootsecurityconcise.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * security登录认证失败处理器
 */
@Component
public class SecurityAuthFailureHandler implements AuthenticationFailureHandler {

    // 添加result属性，可以让第三方异常调用，展示异常信息
    private Result result;
    public Result getResult() {
        return result;
    }
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * 验证失败后执行
     * @param request 请求对象
     * @param response 响应对象
     * @param exception security验证失败后的封装对象，包括用户的信息
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 登录的用户验证失败后执行
        response.setContentType("text/json;charset=utf-8");
        System.out.println("failure handler...");

        // 判断是否自定义的result，还是第三方异常调用的result，第三方异常调用时，result已经有值了
        if(result == null){
            Result localResult = new Result();
            localResult.setCode(1);
            localResult.setStatus(500);
            localResult.setMsg("登录失败(用户名或密码错误)!");
            result = localResult;
        }

        // 使用jsckson
        ObjectMapper mapper = new ObjectMapper();
        ServletOutputStream outputStream = response.getOutputStream();
        mapper.writeValue(outputStream, result);

        outputStream.flush();
        outputStream.close();

        // PrintWriter writer = response.getWriter();
        // writer.println("{\"msg\":\"登录失败(用户名或密码错误)！\"}");
        // writer.println("""
        //         {
        //             "state": 0,
        //             "msg": "登录失败(用户名或密码错误)!"
        //         }
        //         """);
        // writer.flush();
        // writer.close();
    }
}
