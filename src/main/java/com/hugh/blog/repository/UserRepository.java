package com.hugh.blog.repository;

import com.hugh.blog.po.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {

    User findUserByUsernameAndPassword(String username,String password);

}
