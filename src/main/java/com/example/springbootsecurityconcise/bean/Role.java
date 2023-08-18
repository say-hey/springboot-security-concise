package com.example.springbootsecurityconcise.bean;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Data;

/**
 * 角色表
 */
@Data
public class Role {

    Integer id;
    String role;
}
