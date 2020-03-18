package com.hugh.blog.service;

import com.hugh.blog.po.User;

public interface UserService {

    User checkUser(String username, String password);

}
