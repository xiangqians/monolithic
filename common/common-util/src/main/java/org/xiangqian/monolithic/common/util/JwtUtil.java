package org.xiangqian.monolithic.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 什么是JWT
 * JSON Web Token（JWT）是一个轻量级的认证规范，这个规范允许我们使用JWT在用户和服务器之间传递安全可靠的信息。其本质是一个token，是一种紧凑的URL安全方法，用于在网络通信的双方之间传递。
 *
 * @author xiangqian
 * @date 19:13 2024/06/01
 */
public class JwtUtil {

    /**
     * 生成JWT
     *
     * @param claims 声明数据
     * @param exp    过期时间
     * @return token
     */
    public static String generate(Map<String, Object> claims, Duration exp, SecretKey key) {
        // 签名算法
        SecureDigestAlgorithm alg = Jwts.SIG.HS256;

        // JWT的构成：头部、载荷与签名
        return Jwts.builder()
                // 1、头部（Header）
                .header()
                .add("typ", "JWT") // JWT
                .add("alg", alg.getId()) // 签名算法
                .and()

                // 2、载荷：存放有效信息
                // 2.1、标准中注册的声明
                // 签发者（iss，Issuer）
                .issuer("xiangqian")
                // 签发时间（iat，Issued At）
                .issuedAt(new Date())
                // 主题（sub，Subject）
                .subject("JWT")
                // 生效时间（nbf，Not Before）
                //.notBefore()
                // 过期时间（exp，Expiration time）
                .expiration(Date.from(Instant.now().plusSeconds(exp.getSeconds())))
                // 接收者（aud，Audience）
                //.audience()
                // 编号（jti，JWT ID）
                .id(UUID.randomUUID().toString())

                // 2.2、载荷/Payload
                // 公共的声明可以添加任何的信息，一般添加用户的相关信息或其他业务需要的必要信息。但不建议添加敏感信息，因为该部分在客户端可解密
                .claims(claims)


                // 3、签名
                .signWith(key, // 密钥key
                        alg) // 签名算法
                .compact();
    }

    public static String generate(Map<String, Object> claims, Duration exp, String key) {
        return generate(claims, exp, Keys.hmacShaKeyFor(key.getBytes()));
    }

    /**
     * 解析声明数据
     *
     * @param token
     * @param key
     * @return
     */
    public static Jws<Claims> parseClaims(String token, SecretKey key) {
        Jws<Claims> jws = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        return jws;
    }

    public static Jws<Claims> parseClaims(String token, String key) {
        return parseClaims(token, Keys.hmacShaKeyFor(key.getBytes()));
    }

    /**
     * 判断token是否有效
     *
     * @param token
     * @param key
     * @return
     */
    public static boolean isValid(String token, SecretKey key) {
        try {
            parseClaims(token, key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValid(String token, String key) {
        return isValid(token, Keys.hmacShaKeyFor(key.getBytes()));
    }

}
