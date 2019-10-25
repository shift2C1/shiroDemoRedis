package com.pipichao.springboot.withDataBase.realm;

import com.pipichao.springboot.withDataBase.dao.UserMapper;
import com.pipichao.springboot.withDataBase.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
//@Service
public class CustomRealm extends AuthorizingRealm {
    @Autowired
    private UserMapper userMapper;


    /**
     * //授权之前必须要先登录 不然会抛异常
     * */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();
        List list =principalCollection.asList();
        for (Object o:list
             ) {
            System.out.println(o);
        }
        // 从数据库中根据用户名获取角色数据
        Set<String> roles = getRolesByUserName(username);
        // 从数据库中根据用户名获取权限数据
        Set<String> permissions = getPermissionbyUsername(username);

        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setStringPermissions(permissions);
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("数据库数据认证开始");
        String username=(String)authenticationToken.getPrincipal();
        String passwrd=getUserPasswordByUserName(username);

        //注意，数据库中的user的密码必须是要经过md5加密，不然还是会抛出异常！！！！！
        SimpleAuthenticationInfo authenticationInfo=new SimpleAuthenticationInfo(username,passwrd,"customRealm");
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("pipichao"));
        log.info("数据库数据认证结束");
        return authenticationInfo;
    }


    //查询用户信息
    private String getUserPasswordByUserName(String username){
        User user=userMapper.getUserByUsername(username);
        if (user==null){
            return null;
        }
        return user.getPassword();
    }
/**
 *
 * */
    //查询用户角色
    private Set<String> getRolesByUserName(String username){
        List<String> list=userMapper.getRolesByUserName(username);
        //用户角色表中 必须是 ：
        //                      用户1----权限1
        //                      用户1----权限2
        //这样的结构
        Set<String> set=new HashSet<>(list);
        return set;
    }

    //查询用户权限
    private Set<String> getPermissionbyUsername(String username) {

        List<String> permissionList=userMapper.getPermissionsByUsername(username);
//        sets.add("user:delete");
//        sets.add("user:add");
//        sets.add("user:get");
//        sets.add("admin:get");
        Set<String> sets = new HashSet<String>(permissionList);
        return sets;
    }
}
