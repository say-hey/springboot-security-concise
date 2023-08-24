package com.example.springbootsecurityconcise.repository;

import com.example.springbootsecurityconcise.bean.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 角色表接口
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    // 根据角色名查询角色对象
    Role findRoleByRole(String role);
}
