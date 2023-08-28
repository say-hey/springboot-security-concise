package com.example.springbootsecurityconcise.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * home页面路由
 */
@RestController
public class HomeController {

    @GetMapping("/home/l0")
    public String l0(){
        return "you is a dog/cat";
    }

    @GetMapping("/home/l1")
    public String l1(){
        return "you really a dog";
    }

    @GetMapping("/home/l2")
    public String l2(){
        return "you really a cat";
    }
}
