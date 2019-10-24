package com.pipichao.springboot.withDataBase.config;

import com.pipichao.springboot.withDataBase.session.CustomSessionManager;
import com.pipichao.springboot.withDataBase.session.RedisSessionDao;
import org.apache.shiro.session.mgt.SessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableCaching
public class RedisConfig {
    //redis配置

    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public JedisPool redisPoolFactory(){
        JedisPoolConfig config=new JedisPoolConfig();
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        JedisPool jedisPool=new JedisPool(config,host,port);
        return jedisPool;
    }


    @Bean
    public SessionManager sessionManager(){
        CustomSessionManager sessionManager=new CustomSessionManager();
        sessionManager.setSessionDAO(redisSessionDao());
        return sessionManager;
    }

    @Bean
    public RedisSessionDao redisSessionDao(){
        return new RedisSessionDao();
    }
}
