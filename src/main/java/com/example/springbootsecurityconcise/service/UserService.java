package com.example.springbootsecurityconcise.service;

import com.example.springbootsecurityconcise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


}
