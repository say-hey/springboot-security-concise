package com.example.springbootsecurityconcise.exception;


import org.springframework.security.core.AuthenticationException;

/**
 * 验证码异常处理，在过滤器处理验证码之前
 * 不要导错包org.springframework.security.core.AuthenticationException;
 */
public class VerificationException extends AuthenticationException {

    public VerificationException(){
        super("验证码错误，请重新输入！");
    }

    public VerificationException(String msg){
        super(msg);
    }

}
