package com.example.springbootsecurityconcise.bean;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {

    @Id
    // 主键自动增长
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    /**
     * 多对多关系会在创建用户和新角色时级联新增，关联表为user_role，当前对象在关联表对应的外键，和另一方在关联表中对应的外键
     * cascade:级联操作，如保存、删除时级联的行为
     * joinColumns:在关联表中的外键名
     * inverseJoinColumns:另一方在关联表中的外键名
     */
    @ManyToMany(targetEntity = Role.class, cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "u_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "r_id", referencedColumnName = "id")})
    List<Role> roles = new ArrayList<>();


    /**
     * 重写toString()方法，否则在sout输出时，会导致两个对象的toString()相互调用，现在需要去掉一方的关联字段输出
     * java.lang.StackOverflowError
     * @return
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}';
    }
}
