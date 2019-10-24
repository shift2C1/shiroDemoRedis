package com.pipichao.springboot.withDataBase.session;

import com.pipichao.springboot.withDataBase.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class RedisSessionDao extends AbstractSessionDAO {

    @Autowired
    private RedisUtil redisUtil;

    private static final String SHIRO_SHIRO_PREFIX = "pipichao-session";

    /**
     * 使用sessionId + 前缀的二进制形式作为key
     */
    private byte[] getKey(String key) {
        return (SHIRO_SHIRO_PREFIX + key).getBytes();
    }

    private void saveSession(Session session) {
        byte[] key = getKey(session.getId().toString());
        // 序列化为byte数组
        byte[] value = SerializationUtils.serialize(session);

        //session设置10分钟有效 放到redis中
        redisUtil.set(key, value);
        //时间以秒为单位
        redisUtil.expire(key, 600);//10分钟
    }

    @Override
    protected Serializable doCreate(Session session) {
        log.info("RedisSessionDao生成session开始");
        Serializable sessionId=generateSessionId(session);
        assignSessionId(session,sessionId);
        saveSession(session);
        log.info("RedisSessionDao生成session结束");
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        log.info("RedisSessionDao读取session开始");
        if (sessionId==null){
            return  null;
        }
        byte[] key = getKey(sessionId.toString());
        byte[] value = redisUtil.get(key);
        // 反序列化为sesison对象
        log.info("RedisSessionDao读取session结束");
        return (Session) SerializationUtils.deserialize(value);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        log.info("RedisSessionDao更新session开始");
        saveSession(session);///同时把时间更新了
        log.info("RedisSessionDao更新session结束");
    }

    @Override
    public void delete(Session session) {
        log.info("RedisSessionDao删除session开始");
        if(session == null || session.getId() == null){
            return;
        }
        byte[] key = getKey(session.getId().toString());
        log.info("RedisSessionDao删除session结束");
        redisUtil.delete(key);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        log.info("RedisSessionDao获取激活的session开始");
        Set<byte[]> keys = redisUtil.getKeys(SHIRO_SHIRO_PREFIX);
        Set<Session> sessions = new HashSet<>();
        if(CollectionUtils.isEmpty(keys)) {
            return sessions;
        }
        for(byte[] key : keys) {
            Session session = (Session) SerializationUtils.deserialize(redisUtil.get(key));
            sessions.add(session);
        }
        log.info("RedisSessionDao获取激活的session结束");
        return sessions;
    }
}
