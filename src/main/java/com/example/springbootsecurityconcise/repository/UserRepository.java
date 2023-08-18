package com.example.springbootsecurityconcise.repository;

import com.example.springbootsecurityconcise.bean.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户表接口
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByUsername(String username);
}
