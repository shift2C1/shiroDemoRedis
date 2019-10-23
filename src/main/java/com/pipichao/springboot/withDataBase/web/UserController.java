package com.pipichao.springboot.withDataBase.web;

import com.pipichao.springboot.withDataBase.dao.UserMapper;
import com.pipichao.springboot.withDataBase.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Controller("dbUserController")//登录成功后会找到配置的 登录成功页面或者 登录失败页面
@RestController("dbUserController")//即使配置了 登录成功或者登陆失败页面也只会返回数据：不知道是否和找不到 degnlu成功的html页面有关
@RequestMapping("/db")
@Slf4j
public class UserController {
//    @Autowired
//    private UserMapper userMapper;

    @RequestMapping("/login")
    public String login(User user){
        log.info("登陆开始.......");
        Subject subject=SecurityUtils.getSubject();
        UsernamePasswordToken token=new UsernamePasswordToken(user.getUsername(), user.getPassword());
        System.out.println("token："+token);
        subject.login(token);
//        userMapper.getRolesByUserName(user.getUsername());
        log.info("登录结束.......");
        return "登录成功";
    }


    @RequestMapping("/testRoles")
    @RequiresRoles("admin")
    public String testRoles(){
        log.info("角色验证");
        return "角色认证成功";
    }
    @RequestMapping("/testPermission")
    @RequiresPermissions({"admin:get","user:get"})
    public String testPermission(){
        log.info("权限验证");
        return "权限验证成功";
    }
}
