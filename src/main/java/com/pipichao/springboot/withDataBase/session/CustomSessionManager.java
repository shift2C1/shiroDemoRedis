package com.pipichao.springboot.withDataBase.session;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

@Slf4j
public class CustomSessionManager extends DefaultWebSessionManager {

    /**
     * 自定义sessionManager 用了减少多次访问redis问题
     */

    //retrieve: 恢复，取回
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;
        if(sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey)sessionKey).getServletRequest();
        }
        // 先从request中获取session
        if(request != null && sessionId != null){
            Session session = (Session) request.getAttribute(sessionId.toString());
            if(session != null) {
                return session;
            }
        }
        // 如果request中没有获取到，从原始方法（redis）中获取，存入到request中
        Session session = super.retrieveSession(sessionKey);
        if(request != null && sessionId != null) {
            request.setAttribute(sessionId.toString(), session);
        }
        log.info("自定义session管理器 恢复session，sessionId:"+sessionId+",session:"+session.getAttribute("2"));
        return session;
    }
}
