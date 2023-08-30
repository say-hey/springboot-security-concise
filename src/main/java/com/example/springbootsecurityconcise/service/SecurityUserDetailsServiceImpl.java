package com.example.springbootsecurityconcise.service;

import com.example.springbootsecurityconcise.bean.Role;
import com.example.springbootsecurityconcise.bean.User;
import com.example.springbootsecurityconcise.repository.UserRepository;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("用户 " + username + " 登录失败，用户名不存在！");
        }
        // System.out.println("登录用户：" + ((Role)user.getRoles()).getRole());

        System.out.println("登录用户：" + user);


        // 方式一：取出所有角色转为集合，最后转为数组返回
        // List<String> roles = user.getRoles().stream().map(Role::getRole).toList();

        // 方式二：添加权限而不是角色
        List<Role> roles = user.getRoles();
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRole()));
        }

        // 方法三：在自定义的User实现UserDetails后，利用上方方式实现getAuthorities()方法，直接返回
        Collection<? extends GrantedAuthority> authorities1 = user.getAuthorities();

        // 权限和角色在前缀上不同，权限会自动加上前缀ROLE_，roles()方法点进去就是GrantedAuthority，权限表示角色所能访问的资源
        // GrantedAuthority : ROLE_admin
        // Role : admin

        // security.User的所有信息，可以在自定义的User中implements UserDetails并实现方法
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                // 设置角色信息
                // .roles() // cannot start with ROLE_ (it is automatically added)
                // .roles(roles.toString().split(","))
                // 设置权限信息
                // .authorities(authorities)    // 方式二
                .authorities(authorities1)      // 方式三
                // 账号是否过期
                // .accountExpired()
                // 凭证/证书是否过期
                // .credentialsExpired()
                // 账号是否锁定
                // .accountLocked()
                // 账号是否可用
                // .disabled()

                .build();

        // 其他方式代码：角色集合
        // List<Role> roleList=roleService.findRoleByUser(user.getUserGuid());
        // Set<GrantedAuthority> authorities = new HashSet<>();
        // for (Role role : roleList) {
        //     authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getRoleKey()));
        // }
        // myUserDetail.setAuthorities(authorities);
    }
}
