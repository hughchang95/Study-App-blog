package com.hugh.blog.service.impl;

import com.hugh.blog.po.User;
import com.hugh.blog.repository.UserRepository;
import com.hugh.blog.service.UserService;
import com.hugh.blog.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {
        //防止数据库明文存放
        return userRepository.findUserByUsernameAndPassword(username, MD5Utils.code(password));
    }

}
