package com.pipichao.springboot.withDataBase.web;

import com.alibaba.fastjson.JSONObject;
import com.pipichao.springboot.withDataBase.common.MsgException;
import com.pipichao.springboot.withDataBase.dao.UserMapper;
import com.pipichao.springboot.withDataBase.entity.User;
import com.pipichao.springboot.withDataBase.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@Controller("dbUserController")//登录成功后会找到配置的 登录成功页面或者 登录失败页面
@RestController("dbUserController")//即使配置了 登录成功或者登陆失败页面也只会返回数据：不知道是否和找不到 degnlu成功的html页面有关
@RequestMapping("/db")
@Slf4j
//@RequiresPermissions("")//整个类的方法都需要权限
public class UserController {
//    @Autowired
//    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    public String login( User user){
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
//    @RequiresRoles("admin")
//    @RequiresUser
    public String testRoles(@RequestBody User user){
        log.info("角色验证");
        JSONObject result=new JSONObject();
        try {
//            Subject subject=SecurityUtils.getSubject();
//            boolean testRoles=subject.hasRole("admin");
//            System.out.println("测试认证角色："+testRoles);
            userService.testRole(user);
            result.put("code","200");
            result.put("msg","角色认证成功");
            return result.toJSONString();
        }catch (UnauthenticatedException e){
            e.printStackTrace();
//            log.info("shibai");
            try {
                throw new MsgException("-1","未登录");
            } catch (MsgException e1) {
                e1.printStackTrace();
                result.put("code",e1.getCode());
                result.put("msg",e1.getMsg());
                return result.toJSONString();
            }
        }catch (AuthorizationException e){
            e.printStackTrace();
            try {
                throw new MsgException("-1","授权失败：没有该权限");
            } catch (MsgException e1) {
                e1.printStackTrace();
                result.put("code",e1.getCode());
                result.put("msg",e1.getMsg());
                return result.toJSONString();
            }
        }


    }
    @RequestMapping("/testPermission")
//    @RequiresPermissions({"admin/get","user/get"})
    public String testPermission(){
        log.info("权限验证");
        return "权限验证成功";
    }

    @RequestMapping("/logout")
    public String logout(User user){
        Subject subject=SecurityUtils.getSubject();
        System.out.println(subject.getPreviousPrincipals());
        System.out.println(subject.getPrincipal());
        System.out.println(subject.getPrincipals());
        SecurityUtils.getSubject().logout();
        return "退出成功";
    }
}
