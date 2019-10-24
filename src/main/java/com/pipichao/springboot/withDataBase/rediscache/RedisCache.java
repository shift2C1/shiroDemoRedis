package com.pipichao.springboot.withDataBase.rediscache;

import com.pipichao.springboot.withDataBase.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@Component
@Slf4j
public class RedisCache<K,V> implements Cache<SimplePrincipalCollection, SimpleAuthorizationInfo> {

    private final String CACHE_PREFIX = "pipichao-cache";

    @Autowired
    private RedisUtil redisUtil;

    private byte[] getKey(String k) {
        if(k instanceof String) {
            return (CACHE_PREFIX + k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }



    @Override
    public SimpleAuthorizationInfo get(SimplePrincipalCollection k) throws CacheException {
        // 这里扩展可以加入echache二级缓存机制
//        System.out.println("从redis中获取用户角色数据");
        byte[] value = redisUtil.get(getKey(k.getPrimaryPrincipal().toString()));
        if(value != null) {
            SimpleAuthorizationInfo authorizationInfo=(SimpleAuthorizationInfo) SerializationUtils.deserialize(value);
            Set<Permission> permissionSet=authorizationInfo.getObjectPermissions();
            Set<String> roleSet=authorizationInfo.getRoles();
            if (roleSet!=null && roleSet.size()>0){
                Iterator<String> iterator=roleSet.iterator();
                while (iterator.hasNext()){
                    log.info("从缓存中获取角色：{"+k+":"+iterator.next()+"}");
                }

            }
            if (permissionSet!=null && permissionSet.size()>0){
                Iterator<Permission> iterator=permissionSet.iterator();
                while (iterator.hasNext()){
                    log.info("从缓存中获取权限：{"+k+":"+iterator.next()+"}");
                }
            }
            return authorizationInfo;
        }
        return null;
    }

    @Override
    public SimpleAuthorizationInfo put(SimplePrincipalCollection k, SimpleAuthorizationInfo v) throws CacheException {
//        System.out.println("将获取用户角色数据存入到redis中");
        byte[] key = getKey(k.getPrimaryPrincipal().toString());
        byte[] value = SerializationUtils.serialize(v);

        redisUtil.set(key, value);
        redisUtil.expire(key, 600);
        Set<String> roleSet=v.getRoles();
        Set<String> permissionSet=v.getStringPermissions();
        if (roleSet!=null && roleSet.size()>0){
            Iterator<String> iterator=roleSet.iterator();
            while (iterator.hasNext()){
                log.info("将角色存到缓存存中：{"+k+":"+iterator.next()+"}");
            }
        }
        if (permissionSet!=null && permissionSet.size()>0){
            Iterator<String> iterator=permissionSet.iterator();
            while (iterator.hasNext()){
                log.info("将权限存到缓存存中：{"+k+":"+iterator.next()+"}");
            }
        }
        return v;
    }

    @Override
    public SimpleAuthorizationInfo remove(SimplePrincipalCollection k) throws CacheException {
        byte[] key = getKey(k.getPrimaryPrincipal().toString());
        byte[] value = redisUtil.get(key);
        redisUtil.delete(key);
        if(value != null) {
            SimpleAuthorizationInfo valueDeSeri=(SimpleAuthorizationInfo) SerializationUtils.deserialize(value);
            log.info("从缓存中移除数据：{"+k+":"+valueDeSeri+"}");
            return valueDeSeri;
        }
        return null;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<SimplePrincipalCollection> keys() {
        return null;
    }

    @Override
    public Collection<SimpleAuthorizationInfo> values() {
        return null;
    }
}
