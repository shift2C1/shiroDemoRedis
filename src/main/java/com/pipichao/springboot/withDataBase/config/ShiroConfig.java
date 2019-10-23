package com.pipichao.springboot.withDataBase.config;

import com.pipichao.springboot.withDataBase.realm.CustomRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    //第二步：初始化spring时候加载

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager){
        ShiroFilterFactoryBean factoryBean=new ShiroFilterFactoryBean();

        factoryBean.setSecurityManager(securityManager);
        //登录地址
        factoryBean.setLoginUrl("/static/login.html");
        //认证成功地址
        factoryBean.setSuccessUrl("/index.html");
        //认证失败地址
        factoryBean.setUnauthorizedUrl("/403.html");

        ////自定义拦截器
        Map<String, Filter> filterMaps=new LinkedHashMap<>();//LinkedHashMap 保证顺序
        //限制同一帐号同时在线的个数
//        filterMaps.put("kickout",kickoutSessionControlFilter())
        factoryBean.setFilters(filterMaps);


        // 权限控制map.过滤器链
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        filterChainDefinitionMap.put("/login", "anon");//登录页面匿名访问
        filterChainDefinitionMap.put("/*", "authc");//其他所有页面需要认证

        // 配置不会被拦截的链接 顺序判断
        // 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
        // 从数据库获取动态的权限 //
        // filterChainDefinitionMap.put("/add", "perms[权限添加]");
        // <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        // logout这个拦截器是shiro已经实现好了的。
        // 实际开发中需要从数据库获取 /*
//         List<SysPermissionInit> list = sysPermissionInitService.selectAll();
//         for (SysPermissionInit sysPermissionInit : list)
//         {
//             filterChainDefinitionMap.put(sysPermissionInit.getUrl(),
//             sysPermissionInit.getPermissionInit());
//         }

        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);



        return factoryBean;
    }


    // 第一步：初始化spring时候加载

    @Autowired
    private CustomRealm customRealm;
    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager=new DefaultWebSecurityManager();


        //需要从spring容器中获取
//        CustomRealm customRealm=new CustomRealm();

        // 2.加密配置
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("md5");//设置加密算法名称
        matcher.setHashIterations(1);//设置加密次数

        customRealm.setCredentialsMatcher(matcher);


        //
        securityManager.setRealm(customRealm);
        return securityManager;
    }
}
