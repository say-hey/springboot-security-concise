package com.example.springbootsecurityconcise.bean;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * 角色表
 */
@Data
@Entity
@NoArgsConstructor
@Table
public class Role {


    @Id
    // 自增
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String role;

    // 用户角色多对多，属性要实例化
    @ManyToMany(mappedBy = "roles")
    Set<User> users = new HashSet<>();


    // 构造器
    public Role(String role){
        this.role = role;
    }
}
