package com.pipichao.springboot.withDataBase.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Iterator;
import java.util.Set;
@Component
public class RedisUtil {
    /**
     * redis操作工具类
     *
     * */
    @Autowired
    private JedisPool jedisPool;//获取redis配置文件中的 redis连接池
    private Jedis getResource(){
        return jedisPool.getResource();
    }
    public void set(byte[] key,byte[] value){
        Jedis jedis=getResource();
        try {
            jedis.set(key,value);
        }catch (Exception e){
            jedis.close();
        }finally {
            jedis.close();
        }
    }

    //过期
    public void expire(byte[] key,int time){
        Jedis jedis=getResource();
        try {
            jedis.expire(key, time);
        }catch (Exception e){
            jedis.close();
        }finally {
            jedis.close();
        }
    }

    public byte[] get(byte[] key){
        Jedis jedis=getResource();
        byte [] value=null;
        try {
           value= jedis.get(key);
        }catch (Exception e){
            jedis.close();
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return value;
    }

    public void delete(byte[] key){
        Jedis jedis=getResource();
        try {
            jedis.del(key);
        } catch (Exception e) {
            jedis.close();
            e.printStackTrace();
        }finally {
            jedis.close();
        }
    }

    public Set<byte[]> getKeys(String shiroPrefix){
        Jedis jedis=getResource();
        Set<byte[]> keys=null;
        try {
            keys= jedis.keys((shiroPrefix + "*").getBytes());
        } catch (Exception e){
            jedis.close();
        }finally {
            jedis.close();
        }
        return keys;
    }

//    public static void main(String[] args) {
//        RedisUtil redisUtil=new RedisUtil();
//        redisUtil.set("testkey1".getBytes(),"testvalue1".getBytes());
//        System.out.println(redisUtil.get("testkey1".getBytes()).toString());
//        redisUtil.delete("testkey1".getBytes());
//        System.out.println(redisUtil.get("testkey1".getBytes()).toString());
//        JedisPool jedisPool=new JedisPool();
//        Jedis jedis=jedisPool.getResource();
////        jedis.set("testkey2","testvalue2");
//        System.out.println(jedis.get("testkey2"));
//        Set<String> keys=jedis.keys("*");
//        Iterator<String> iterator=keys.iterator();
//        while (iterator.hasNext()){
//            System.out.println(iterator.next());
//        }
//        jedis.close();
//    }
}
