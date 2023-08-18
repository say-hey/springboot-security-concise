package com.example.springbootsecurityconcise.bean;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户表
 * 用户表和角色表的对应关系，
 * 一对多：一个用户对应多个角色，添加用户时，从角色表中取值，一般不会联动
 * 多对多：多个用户对应多个角色，添加用户时，可以从角色表取值，也可以直接创建新的角色，会联动两个表一起添加
 */

@Data
@Entity
@Table
public class User {

    @Id
    // 主键自动增长
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String username;
    String password;

    // 用户角色多对多，关联表为user_role，当前对象在关联表对应的外键，和另一方在关联表中对应的外键，并且要实例化
    @ManyToMany(targetEntity = Role.class, cascade = CascadeType.ALL)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "u_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "r_id", referencedColumnName = "id")})
    Set<Role> roles = new HashSet<>();

    // 一对多
//    @OneToMany
//    Set<Role> roles = new HashSet<>();
}
