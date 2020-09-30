package com.demo01.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.demo01.entity.User;

import java.util.Date;

public class JWTUtil {

    /**
     * 过期时间一周
     */
    private static final long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;

    public static final String USER_NAME = "USER_NAME";

    public static final String USER_ID = "USER_ID";

    /**
     * 校验token是否正确
     *
     * @param token  密钥
     * @param secret
     * @return 是否正确
     */
    public static boolean verify(String token, String username, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim(USER_NAME, username)
                    .build();
            verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 获得token中的信息无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(USER_NAME).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static Integer getUserId(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(USER_ID).asInt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }


    /**
     * 生成签名,5min后过期
     *
     *  username 用户名
     *  secret   用户的密码
     * @return 加密的token
     */
    public static String sign(User user) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(user.getPassWord());
        // 附带username信息
        return JWT.create()
                .withClaim(USER_NAME, user.getUserName())
                .withClaim(USER_ID, user.getId())
                .withExpiresAt(date)
                .sign(algorithm);
    }

    public static Boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            final Date expiration = jwt.getExpiresAt();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }

    }

    public static void main(String[] args) {
        Algorithm algorithm = Algorithm.none();
        String s = JWT.create()
                .withClaim("", "")
                .withClaim("", "")
                .sign(algorithm);
        System.out.println(s);
    }
}
