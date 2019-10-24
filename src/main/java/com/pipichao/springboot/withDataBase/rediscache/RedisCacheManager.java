package com.pipichao.springboot.withDataBase.rediscache;

import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

public class RedisCacheManager implements CacheManager {
    @Autowired
    private RedisCache<String, SimpleAuthorizationInfo> redisCache;
    @Override
    public  Cache<SimplePrincipalCollection, SimpleAuthorizationInfo> getCache(String s) throws CacheException {
//        RedisCache<SimplePrincipalCollection, SimpleAuthorizationInfo> redisCache=new RedisCache();
        return redisCache;
    }
}
