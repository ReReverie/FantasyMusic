package com.fantasy.fm.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    /**
     * 生成jwt
     *
     * @param secretKey  jwt秘钥
     * @param expireTime jwt过期时间(毫秒)
     * @param claims     设置的信息
     * @return 生成的jwt字符串
     */
    public static String generateToken(String secretKey, Long expireTime, Map<String, Object> claims) {
        //设置jwt时间,并转换成Date类型
        long expiration = System.currentTimeMillis() + expireTime;
        Date exp = new Date(expiration);

        //获取配置的秘钥转换成SecretKey对象
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        //构建JWT,并返回生成的jwt字符串
        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .expiration(exp)
                .compact();
    }

    /**
     * 解析jwt
     *
     * @param secretKey jwt秘钥
     * @param token     加密后的token
     * @return 解析后的Claims对象
     */
    public static Claims parseToken(String secretKey, String token) {
        //获取配置的秘钥转换成SecretKey对象
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());

        //解析jwt字符串,并返回Claims对象
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
