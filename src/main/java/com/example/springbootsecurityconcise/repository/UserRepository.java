package com.example.springbootsecurityconcise.repository;

import com.example.springbootsecurityconcise.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户表接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // 根据用户名查找对象
    User findUserByUsername(String username);

}
