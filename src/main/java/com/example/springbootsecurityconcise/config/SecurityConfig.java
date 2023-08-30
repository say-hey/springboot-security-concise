package com.example.springbootsecurityconcise.config;


import com.example.springbootsecurityconcise.filter.VerificationFilter;
import com.example.springbootsecurityconcise.handler.SecurityAuthFailureHandler;
import com.example.springbootsecurityconcise.handler.SecurityAuthSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;


/**
 * Security配置类
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {


    // 验证成功和失败处理器
    @Autowired
    SecurityAuthSuccessHandler successHandler;
    @Autowired
    SecurityAuthFailureHandler failureHandler;

    /**
     * 密码编码器
     * @return
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     *
     * 和 extends SecurityConfigurerAdapter.configure(AuthenticationManagerBuilder)有什么不同？
     * 之前的做法有在configure(AuthenticationManagerBuilder)中配置auth.userDetailsService(myDetailsService).passwordEncoder(bcry)
     * 在configure(HttpSecurity)中配置http.authorizeHttpRequests()认证
     * 现在同样使用HttpSecurity参数，HttpSecurity：具体的权限控制规则配置
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth->{

                    // 设置url权限，注意所有权限的配置顺序
                    auth.requestMatchers("/home").permitAll();
                    // 验证码
                    auth.requestMatchers("/captcha/**").permitAll();
                    // 静态资源
                    auth.requestMatchers("/js/**").permitAll();
                    auth.requestMatchers("/home/l0").hasRole("USER");
                    auth.requestMatchers("/home/l1/**").hasRole("Dog");
                    auth.requestMatchers("/home/l2/**").hasRole("Cat");
                    auth.anyRequest().authenticated();
                })
                .formLogin(conf->{
                    // 自定义表单登录页
                    // https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
                    conf.loginPage("/login");
                    // 表单登录请求
                    // 登录页是使用表单还是ajax可在login.html修改
                    conf.loginProcessingUrl("/login");
                    // 登录成功处理器，取消defaultSuccessUrl默认登录成功页可以看到效果，如登录失败处理器类似
                    // conf.successHandler(authenticationSuccessHandler());
                    // 登录失败处理器，但此处不能在表单上方显示error信息
                    // conf.failureHandler(authenticationFailureHandler());
                    // 使用handler类
                    conf.successHandler(successHandler);
                    conf.failureHandler(failureHandler);
                    // 默认登录成功页，使用了handler，就不要使用默认登录页，否则handler不起作用
                    // conf.defaultSuccessUrl("/home");
                    // 登录相关请求不需要认证
                    conf.permitAll();
                })
                .logout(conf->{
                    // 登出请求
                    conf.logoutUrl("/logout");
                    conf.logoutSuccessUrl("/login");
                    conf.permitAll();
                })
                // 使用自定义过滤器，并且
                .addFilterBefore(new VerificationFilter(), UsernamePasswordAuthenticationFilter.class)
                // 使用自定义的userDetails认证过程，
                // .userDetailsService(null)
                .csrf(AbstractHttpConfigurer::disable)// 关闭跨站请求伪造保护功能
                .build();
    }

    /**
     * 和上面方法有什么不同？
     * 点进源码都是同一个地方HttpSecurity HttpSecurityConfiguration.httpSecurity()
     * 结果是一样的 Could not autowire. There is more than one bean of 'HttpSecurity' type.
     * @param http
     */
    // @Bean
    // public HttpSecurity configure(HttpSecurity http) throws Exception {
    //     return http.authorizeHttpRequests(auth->{
    //         // 设置url权限
    //         auth.requestMatchers("/home").permitAll();
    //         auth.requestMatchers("/home/l0").hasRole("USER");
    //         auth.requestMatchers("/home/l1/**").hasRole("Dog");
    //         auth.requestMatchers("/home/l2/**").hasRole("Cat");
    //     });
    // }


    /**
     * 仅示例，不使用
     * 登录成功处理器，不使用这个，使用另外新建了一个类
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
     * 仅示例，不使用
     * 登录失败处理器，不使用这个，使用另外新建的类
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
