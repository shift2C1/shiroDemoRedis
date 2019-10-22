package com.pipichao.springboot.withDataBase.dao;

import com.pipichao.springboot.withDataBase.entity.User;

import java.util.List;

public interface UserMapper {
    public User getUserByUsername(String username);
    public List<String> getRolesByUserName(String username);

}
