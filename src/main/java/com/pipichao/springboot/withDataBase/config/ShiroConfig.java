package com.pipichao.springboot.withDataBase.config;

import com.pipichao.springboot.withDataBase.realm.CustomRealm;
import com.pipichao.springboot.withDataBase.rediscache.RedisCacheManager;
import com.pipichao.springboot.withDataBase.session.CustomSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@Slf4j
public class ShiroConfig {

    /**---------------------------------------------------认证---------------------------------------------------------------------*/
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

//    @Autowired
//    private CustomRealm customRealm;
    @Bean
    public CustomRealm getCustomRealm(){
        return new CustomRealm();
    }
    @Bean
    public CustomSessionManager getCustomSessionManager(){
        return new CustomSessionManager();
    }
    @Bean
    public RedisCacheManager getCacheManager(){
        return new RedisCacheManager();
    }
    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager=new DefaultWebSecurityManager();


        //需要从spring容器中获取
//        CustomRealm customRealm=new CustomRealm();


        // 2.加密配置
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("md5");//设置加密算法名称
        matcher.setHashIterations(1);//设置加密次数


        //1:设置验证授权的对比域
        CustomRealm customRealm=getCustomRealm();
        customRealm.setCredentialsMatcher(matcher);
        securityManager.setRealm(customRealm);

        //2:设置session管理
        CustomSessionManager sessionManager=getCustomSessionManager();
//        securityManager.setSessionManager(sessionManager);
        //3:设置缓存管理
        RedisCacheManager cacheManager=getCacheManager();
//        securityManager.setCacheManager(cacheManager);
        return securityManager;
    }


    /**-----------------------------------------授权-------------------------------------------------------------------------------*/
    //Shiro生命周期处理器
    @Bean
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor(){
        log.info("初始化：lifecycleBeanPostProcessor");
        LifecycleBeanPostProcessor postProcessor=new LifecycleBeanPostProcessor();
        return postProcessor;
    }


    /** * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * * @return */

    @Bean
    @DependsOn({"getLifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator creator=new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor advisor=new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager());
        return advisor;
    }
}
