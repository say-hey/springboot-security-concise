package com.example.springbootsecurityconcise.bean;

import lombok.Data;

/**
 * 用户表
 */
@Data
public class User {

    Integer id;
    String username;
    String password;
    String role;
}
