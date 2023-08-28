package com.example.springbootsecurityconcise.bean;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色表
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "role")
public class Role {


    @Id
    // 自增
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "role")
    String role;

    // 用户角色多对多
    @ManyToMany(mappedBy = "roles", cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    List<User> users = new ArrayList<>();

    // 构造器
    public Role(String role){
        this.role = role;
    }

    /**
     * 重写toString()方法，否则在sout输出时，会导致两个对象的toString()相互调用，现在需要去掉一方的关联字段输出
     * java.lang.StackOverflowError
     * @return
     */
    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", role='" + role + '\'' +
                // ", users=" + users +
                '}';
    }
}
