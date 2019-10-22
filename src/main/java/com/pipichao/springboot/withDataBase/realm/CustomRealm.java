package com.pipichao.springboot.withDataBase.realm;

import com.pipichao.springboot.withDataBase.dao.UserMapper;
import com.pipichao.springboot.withDataBase.entity.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomRealm extends AuthorizingRealm {
    @Autowired
    private UserMapper userMapper;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username=(String)authenticationToken.getPrincipal();

        return null;
    }


    //查询用户信息
    private String getUserPasswordByUserName(String username){
        User user=userMapper.getUserByUsername(username);
        if (user==null){
            return null;
        }
        return user.getPassword();
    }

    //查询用户角色
    private Set<String> getRolesByUserName(String username){
        List<String> list=userMapper.getRolesByUserName(username);
        Set<String> set=new HashSet<>(list);
        return set;
    }

    //查询用户权限
    private Set<String> getPermissionbyUsername(String username) {
        Set<String> sets = new HashSet<String>();
        sets.add("user:delete");
        sets.add("user:add");
        return sets;
    }
}
