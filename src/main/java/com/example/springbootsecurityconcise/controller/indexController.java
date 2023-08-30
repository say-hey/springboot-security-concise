package com.example.springbootsecurityconcise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页路由
 */
@Controller
public class indexController {

    @GetMapping("/login")
    public String login(){
        System.out.println("get/index/login");
        return "login";
    }

    @GetMapping("/home")
    public String home(){
        System.out.println("get/home");
        return "home";
    }
}
