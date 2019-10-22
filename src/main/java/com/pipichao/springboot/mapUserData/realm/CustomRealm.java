package com.pipichao.springboot.mapUserData.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomRealm extends AuthorizingRealm {

    //Realm: 域 范围
    //shiro执行过程：先认证 后授权


    //获取密码的md5加密格式
//    public static void main(String[] args) {
//        Md5Hash md5Hash=new Md5Hash("123456","pipichao");
//        System.out.println(md5Hash);//a1fb849055dc86e039704752b42a47d6
//    }

    //模拟数据库的数据
    private Map<String,String> userMap=new HashMap<>();
    {
        userMap.put("zhangsan","a1fb849055dc86e039704752b42a47d6");
        userMap.put("admin","a1fb849055dc86e039704752b42a47d6");
        super.setName("customRealm");
    }



    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        System.out.println("授权前................");
        String userName=(String) principalCollection.getPrimaryPrincipal();
        Set<String> roleSet=getRolesByUsername(userName);
        Set<String> permissionSet=getPermissionbyUsername(userName);

        //
        SimpleAuthorizationInfo authorizationInfo=new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(roleSet);
        authorizationInfo.setStringPermissions(permissionSet);

        System.out.println("授权后.................");
        return authorizationInfo;
    }

    //自定义方法

    //通过用户名获取用户权限
    private Set<String> getPermissionbyUsername(String userName){

        System.out.println("获取用户权限前");
        //假设是数据库获取的信息
        Set<String> permissionSet=new HashSet<>();
        permissionSet.add("user:delete");
        permissionSet.add("user:add");
        System.out.println("获取用户权限后");
        return permissionSet;
    }

    //通过用户名获取用户角色
    private Set<String> getRolesByUsername(String userName){
        System.out.println("获取用户角色前");
        //假设是数据库获取的信息
        Set<String> roleSet=new HashSet<>();
        roleSet.add("user");
        roleSet.add("admin");
        System.out.println("获取用户角色后");
        return roleSet;
    }


    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        System.out.println("认证前.........");
        String userName=(String) authenticationToken.getPrincipal();
        String password=getPassByUserName(userName);

        if (password==null){
            return  null;
        }
        //返回认证信息
        SimpleAuthenticationInfo authenticationInfo=new SimpleAuthenticationInfo(userName,password,"costomRealm");
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("pipichao"));//设置加盐

        System.out.println("认证后..............");
        return authenticationInfo;
    }


    //通过用户名获取密码
    private String getPassByUserName(String userName){
        System.out.println("通过用户名获取用户信息");
        return userMap.get(userName);
    }



}
