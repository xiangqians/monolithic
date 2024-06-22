package org.xiangqian.monolithic.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

/**
 * @author xiangqian
 * @date 19:32 2024/06/01
 */
public class JwtUtilTest {

    public static void main(String[] args) {
        String key = "GkiYzIiLCJpc3MYjQzODLTQ1MWMtYjM1Ny1hMjMwZjE5M2JiOiJ4aWFuZ3FpYW4iLCJpYXQNkYy1hNGY0sInN1YiI6Ikp";

        String token = JwtUtil.generate(Map.of("id", Long.valueOf(1).toString()), Duration.ofSeconds(30), key);
        token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ4aWFuZ3FpYW4iLCJpYXQiOjE3MTcyNDk2NDMsInN1YiI6IkpXVCIsImV4cCI6MTcxNzI5Mjg0MywianRpIjoiNGUxMzI2N2EtNGIxNy00NGNjLTkwZjMtMWQzNzc3Mjg2MjBhIiwiaWQiOjF9.sGgLeDDyy1Nj_r4Dw5P1p7F_zJANedBEq3_hI68BILs";
        System.out.println(token);
        System.out.println(JwtUtil.isValid(token, key));

        Jws<Claims> jws = JwtUtil.parseClaims(token, key);

        JwsHeader header = jws.getHeader();
        System.out.println(header);

        Claims claims = jws.getPayload();
        System.out.println(claims);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        System.out.format("当前时间：" + simpleDateFormat.format(new Date())).println();
        System.out.format("签发时间：" + simpleDateFormat.format(claims.getIssuedAt())).println();
        System.out.format("过期时间：" + simpleDateFormat.format(claims.getExpiration())).println();
    }

}
