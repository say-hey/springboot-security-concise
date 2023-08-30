package com.example.springbootsecurityconcise.filter;

import com.example.springbootsecurityconcise.exception.VerificationException;
import com.example.springbootsecurityconcise.handler.SecurityAuthFailureHandler;
import com.example.springbootsecurityconcise.vo.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.filter.OncePerRequestFilter;
import org.thymeleaf.util.StringUtils;

import javax.naming.AuthenticationException;
import java.io.IOException;

/**
 * 验证码过滤器，使用在UsernamePasswordAuthenticationFilter之前
 */
public class VerificationFilter extends OncePerRequestFilter {

    // 登录失败的handler，在过滤器抛出异常时使用
    private SecurityAuthFailureHandler failureHandler = new SecurityAuthFailureHandler();

    /**
     * 验证码过滤器
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        // 这个/login和去自定义登录页的login重复了，包括logout也没跳出过滤。判断一次
        // 缺少登录成功，错误提示有问题！！原因是设置了defaultSuccessUrl()，同样的有failurehandler也不要设置默认的错误页

        // 验证码只在登录的过程中才使用这个过滤器
        String requestURI = request.getRequestURI();
        // 如果登录页和表单登录请求都使用/login，那么此处要判断是去登录页还是表单登录请求
        String method = request.getMethod();
        if(!"/login".equals(requestURI) || "GET".equals(method)){
            // 不是登录操作，不经过这个过滤器
            filterChain.doFilter(request, response);
        }else{
            try{
                // 验证验证码
                verificationCode(request);
                // 通过
                filterChain.doFilter(request, response);
            }catch (VerificationException e){
                // 验证出现异常时，跳转到表单登录失败的处理器SecurityAuthFailureHandler中
                // 1.在filter中添加handler属性，在这里调用
                // 2.在SecurityAuthFailureHandler中修改，添加一个vo.Result属性，然后判断是正常的handler还是第三方异常跳转过去的
                Result result = new Result();
                result.setCode(1);
                result.setStatus(501);
                result.setMsg("验证码错误，请重新输入！！");
                failureHandler.setResult(result);
                failureHandler.onAuthenticationFailure(request, response, e);
            }

        }


    }

    private void verificationCode(HttpServletRequest request) throws VerificationException {
        // 获取请求中的验证码Code
        String requestCode = request.getParameter("code");
        // 获取session中的验证码Code
        String sessionCode = "";
        HttpSession session = request.getSession();
        Object code = session.getAttribute("code");
        if(code != null){
            sessionCode = (String) code;
        }

        System.out.println("Verificate Captcha: session:" + sessionCode + " |request:" + requestCode);

        // 一次性验证码，使用后销毁
        if(!StringUtils.isEmpty(sessionCode)){
            // 能获取到session中的验证码，说明已经在页面生成了，现在就不能再用了
            session.removeAttribute("code");
        }

        // 判断验证码code是否正确
        if(StringUtils.isEmpty(requestCode) || StringUtils.isEmpty(sessionCode) || !requestCode.equals(sessionCode)){
            // 验证失败
            throw new VerificationException();
        }
    }
}
