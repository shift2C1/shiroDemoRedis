package com.pipichao.springboot.withDataBase.service;

import com.pipichao.springboot.withDataBase.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    public void login(){

    }

    @RequiresRoles({"admin"})
    public void testRole(User user){
        log.info("测试角色认证service");
    }
}
