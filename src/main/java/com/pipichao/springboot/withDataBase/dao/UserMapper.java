package com.pipichao.springboot.withDataBase.dao;

import com.pipichao.springboot.withDataBase.entity.User;

public interface UserMapper {
    public User getUserByUsername(String username);

}
