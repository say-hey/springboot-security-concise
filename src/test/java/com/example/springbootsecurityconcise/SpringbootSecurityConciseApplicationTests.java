package com.example.springbootsecurityconcise;

import com.example.springbootsecurityconcise.bean.Role;
import com.example.springbootsecurityconcise.bean.User;
import com.example.springbootsecurityconcise.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Optional;

@SpringBootTest
class SpringbootSecurityConciseApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Test
	void contextLoads() {

	}

	/**
	 * 查询用户
	 */
	@Transactional(readOnly = true)
	@Test
	void select() {
//		Optional<User> byId = userRepository.findById(12);
//		System.out.println(byId);
	}


	/**
	 * 保存用户和他的角色
	 */
	@Test
	void save() {
		User user = new User();
		user.setUsername("Jerry");
		user.setPassword("123456");
//		Role role = new Role();
//		role.setRole("A");
		Role a = new Role("A");
		Role b = new Role("B");

		user.getRoles().add(a);
		user.getRoles().add(b);
		userRepository.save(user);
	}

}
