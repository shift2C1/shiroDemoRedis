package com.pipichao.springboot.withDataBase.dao;

import com.pipichao.springboot.withDataBase.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface UserMapper {
    public User getUserByUsername(String username);
    public List<String> getRolesByUserName(String username);

}
