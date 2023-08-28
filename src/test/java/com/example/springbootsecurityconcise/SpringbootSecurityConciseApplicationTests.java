package com.example.springbootsecurityconcise;

import com.example.springbootsecurityconcise.bean.Role;
import com.example.springbootsecurityconcise.bean.User;
import com.example.springbootsecurityconcise.repository.RoleRepository;
import com.example.springbootsecurityconcise.repository.UserRepository;
import com.example.springbootsecurityconcise.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
class SpringbootSecurityConciseApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Test
	void contextLoads() {

	}

	/**
	 * 使用加密密码保存
	 */
	@Test
	void bcry() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		User user = new User();
		user.setUsername("admin");
		user.setPassword(bCryptPasswordEncoder.encode("1"));

		// 查询数据库，使用已有角色
		Role role = roleRepository.findById(1).get();
		user.getRoles().add(role);

		// List<Role> list = new ArrayList<>();
		// List<Role> roles = user.getRoles();
		// roles.add(role);
		// list.add(role);
		// user.setRoles(list);


		userRepository.save(user);

		System.out.println(bCryptPasswordEncoder.encode("1"));

	}


	/**
	 * 查询用户
	 */
	// 存在“一/多对多"的情况时，需要添加事务，作为一个整体
	@Transactional(readOnly = true)
	// 如果需要测试删除，添加事务提交，业务中不需要添加
	// @Commit
	@Test
	void select() {
		List<User> all = userRepository.findAll();
		System.out.println(all);

		List<Role> all1 = roleRepository.findAll();
		System.out.println(all1);

		User jerry = userRepository.findUserByUsername("Jerry");
		System.out.println("roles: " + jerry.getRoles());
		System.out.println("authorities: " + jerry.getAuthorities());

		List<String> list = jerry.getRoles().stream().map(Role::getRole).toList();
		String[] var3 = list.toString().split(",");

		// String[] roles = (String[]) jerry.getRoles().stream().map(Role::getRole).toList().toArray();
		System.out.println(list.toString());

	}


	/**
	 * 保存用户和他的角色，直接添加会导致重复
	 */
	@Test
	void save() {
		// 先验证用户名是否重复
		User jerry = userRepository.findUserByUsername("Jerry");
		if(jerry != null){
			System.out.println("用户名已存在，请重新输入");
			return;
		}
		// 新建用户
		User user = new User();
		user.setUsername("Jerry");
		user.setPassword("123456");

		// 这里应该提供的是指定的角色，否则不能添加
		Role cat = roleRepository.findRoleByRole("Cat");
		if(cat == null){
			System.out.println("角色不存在，请重新选择");
			return;
		}

		// ×新建角色，除非是新角色，否则不能使用新建角色进行设置，不然角色数据会重复
		Role a = new Role("Cat");
		// Role b = new Role("B");

		// √不能使用新建角色，否则数据库角色会重复，使用从数据库中查询出来的角色，且有id属性就可重用
		Role role = roleRepository.findById(1).get();

		// 取出用户角色组，添加新角色/旧角色
		user.getRoles().add(a);
		// 保存用户
		userRepository.save(user);
	}


}
