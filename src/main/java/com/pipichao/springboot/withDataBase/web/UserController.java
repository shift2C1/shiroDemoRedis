package com.pipichao.springboot.withDataBase.web;

import com.pipichao.springboot.withDataBase.dao.UserMapper;
import com.pipichao.springboot.withDataBase.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller("dbUserController")
@RequestMapping("/db")
@Slf4j
public class UserController {
    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/login")
    public void login(User user){
        log.info("登陆开始.......");
        Subject subject=SecurityUtils.getSubject();
        UsernamePasswordToken token=new UsernamePasswordToken(user.getUsername(), user.getPassword());
        subject.login(token);
//        userMapper.getRolesByUserName(user.getUsername());
        log.info("登录结束.......");
//        return "登录成功";
    }
}
