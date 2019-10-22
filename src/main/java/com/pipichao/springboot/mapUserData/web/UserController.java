package com.pipichao.springboot.mapUserData.web;

import com.pipichao.springboot.mapUserData.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
@Slf4j
public class UserController {
    @RequestMapping("/login")
    public String login(User user){
        Subject subject= SecurityUtils.getSubject();
        UsernamePasswordToken token=new UsernamePasswordToken(user.getUsername(),user.getPassword());
        try {
            System.out.println("登录前....");
            subject.login(token);
            System.out.println("登录后。。。");
            return "okay";
        }catch (Exception e){
            return e.getMessage();
        }
    }
    @RequestMapping("/other")
    public String otherRequest() {
        log.info("其他接口 测试是否需要权限");
        return "其他接口";
    }
}
