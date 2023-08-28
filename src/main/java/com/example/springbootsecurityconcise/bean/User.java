package com.example.springbootsecurityconcise.bean;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
// 自定义的User可以实现 implements UserDetails 接口，需要完成方法如是否可用，是否锁定，是否过期，角色集合等，同时在数据库中添加这些字段
// 实现这个方法可用于扩展，也可以不实现
public class User implements UserDetails{

    @Id
    // 主键自动增长
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "username")
    String username;

    @Column(name = "password")
    String password;

    // 过期
    @Column(name = "isAccountNonExpired")
    Boolean isAccountNonExpired;
    // 锁定
    @Column(name = "isAccountNonLocked")
    Boolean isAccountNonLocked;
    // 凭证
    @Column(name = "isCredentialsNonExpired")
    Boolean isCredentialsNonExpired;
    // 启用
    @Column(name = "isEnabled")
    Boolean isEnabled;
    // 权限
    // List<GrantedAuthority> authorities;


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
                ", isAccountNonExpired=" + isAccountNonExpired +
                ", isAccountNonLocked=" + isAccountNonLocked +
                ", isCredentialsNonExpired=" + isCredentialsNonExpired +
                ", isEnabled=" + isEnabled +
                ", roles=" + roles +
                '}';
    }


    // 实现UserDetails后的方法

    /**
     * 获取权限，这里使用的是GrantedAuthority类，在UserDetailsService中出现，用于组装角色权限信息
     * 既然要类型转换，自定义的Role类是否需要和GrantedAuthority有关联？
     *
     * roles: [Role{id=1, role='Cat'}, Role{id=2, role='Dog'}]
     * authorities: [ROLE_Dog, ROLE_Cat]
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<Role> roles = this.getRoles();
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRole()));
        }

        return authorities;
    }

    /**
     * 账户是否过期
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    /**
     * 账户是否锁定
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    /**
     * 凭证是否过期
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    /**
     * 是否启用
     * @return
     */
    @Override
    public boolean isEnabled() {
        return false;
    }
}
