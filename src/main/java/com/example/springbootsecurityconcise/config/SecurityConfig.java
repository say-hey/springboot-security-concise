package com.example.springbootsecurityconcise.config;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;


/**
 * Security配置类
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    /**
     * 密码编码器
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth->{
                    auth.anyRequest().authenticated();
                })
                .formLogin(conf->{
                    // 自定义表单登录页
                    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
                    conf.loginPage("/login");
                    // 表单登录请求
                    conf.loginProcessingUrl("/login");
                    // 登录成功处理器，取消defaultSuccessUrl默认登录成功页可以看到效果，如登录失败处理器类似
                    conf.successHandler(authenticationSuccessHandler());
                    // 登录失败处理器，但此处不能在表单上方显示error信息
                    conf.failureHandler(authenticationFailureHandler());
                    // 默认登录成功页
                    conf.defaultSuccessUrl("/home");
                    // 登录相关请求不需要认证
                    conf.permitAll();
                })
                .logout(conf->{
                    // 登出请求
                    conf.logoutUrl("/logout");
                    conf.logoutSuccessUrl("/login");
                    conf.permitAll();
                })
                .csrf(AbstractHttpConfigurer::disable)// 关闭跨站请求伪造保护功能
                .build();
    }

    /**
     * 登录成功处理器
     * @return
     */
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        // 可转为lambda表达式
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException, IOException {
                System.out.println("登录成功");
                response.setStatus(HttpStatus.OK.value());
                response.setContentType("application/json;charset=UTF-8");
                // 在页面响应“登录成功”
                response.getWriter().write("登录成功");
            }
        };
    }

    /**
     * 登录失败处理器
     */
    public AuthenticationFailureHandler authenticationFailureHandler(){
        // 可转为lambda表达式
        return new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                System.out.println("登录失败");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json;charset=UTF-8");
                // 在页面响应“登录失败”
                response.getWriter().write("登录失败");
            }
        };
    }
}
