package com.demo01.entity;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * created by wangzelong 2019/1/9 10:16
 */
public class JWTToken implements AuthenticationToken {
    // 密钥
    private String token;

    public JWTToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

}
