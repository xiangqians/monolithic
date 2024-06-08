package org.xiangqian.monolithic.biz.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.biz.Assert;
import org.xiangqian.monolithic.biz.CodeException;
import org.xiangqian.monolithic.biz.auth.AuthCode;
import org.xiangqian.monolithic.biz.auth.AuthUtil;
import org.xiangqian.monolithic.biz.auth.model.AuthTokenReq;
import org.xiangqian.monolithic.biz.auth.model.AuthTokenResp;
import org.xiangqian.monolithic.biz.auth.service.AuthService;
import org.xiangqian.monolithic.biz.sys.entity.UserEntity;
import org.xiangqian.monolithic.biz.sys.mapper.UserMapper;
import org.xiangqian.monolithic.util.DateTimeUtil;
import org.xiangqian.monolithic.util.JsonUtil;
import org.xiangqian.monolithic.util.JwtUtil;
import org.xiangqian.monolithic.util.Redis;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * @author xiangqian
 * @date 17:04 2024/06/01
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private SecretKey jwtKey;
    private Duration jwtExp;

    @Autowired
    private Redis redis;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthServiceImpl(@Value("${jwt.key}") String jwtKey, @Value("${jwt.exp}") Duration jwtExp) {
        this.jwtKey = Keys.hmacShaKeyFor(jwtKey.getBytes());
        this.jwtExp = jwtExp;
    }

    @SneakyThrows
    @Override
    public UserEntity getUser(String token) {
        try {
            Jws<Claims> jws = JwtUtil.parseClaims(token, jwtKey);
            Object id = jws.getPayload().get("id");
            Object value = redis.string().get(String.format("%s_%s", id, token));
            if (value != null) {
                return JsonUtil.deserialize(value.toString(), UserEntity.class);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @SneakyThrows
    @Override
    public AuthTokenResp token(AuthTokenReq authTokenReq) {
        UserEntity user = null;
        // 授权类型
        byte type = authTokenReq.getType();
        // 用户名/密码
        if (type == 1) {
            String name = StringUtils.trim(authTokenReq.getNop());
            Assert.notEmpty(name, AuthCode.NAME_NOT_EMPTY);
            String passwd = StringUtils.trim(authTokenReq.getPoc());
            Assert.notEmpty(passwd, AuthCode.PASSWD_NOT_EMPTY);

            user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getName, name)
                    .last("LIMIT 1"));
            Assert.notNull(user, AuthCode.NAME_OR_PASSWD_INCORRECT);
            Assert.isTrue(passwordEncoder.matches(passwd, user.getPasswd()), AuthCode.NAME_OR_PASSWD_INCORRECT);
        }
        // 手机号/密码
        else if (type == 2) {
            String phone = StringUtils.trim(authTokenReq.getNop());
            Assert.notEmpty(phone, AuthCode.PHONE_NOT_EMPTY);
            String passwd = StringUtils.trim(authTokenReq.getPoc());
            Assert.notEmpty(passwd, AuthCode.PASSWD_NOT_EMPTY);

            UserEntity userEntity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getPhone, phone)
                    .last("LIMIT 1"));

        }
        // 手机号/短信验证码
        else if (type == 3) {
            String phone = StringUtils.trim(authTokenReq.getNop());
            Assert.notEmpty(phone, AuthCode.PHONE_NOT_EMPTY);
            String smsCode = StringUtils.trim(authTokenReq.getPoc());
            Assert.notEmpty(smsCode, AuthCode.SMS_CODE_NOT_EMPTY);
        }
        // ?
        else {
            throw new CodeException(AuthCode.TYPE_ILLEGAL);
        }

        String token = null;
        String prefix = String.format("%s_", user.getId());
        Set<String> keys = redis.keyWithPrefix(prefix, 1);
        if (CollectionUtils.isNotEmpty(keys)) {
            token = keys.iterator().next();
            token = token.substring(prefix.length());

            Jws<Claims> jws = JwtUtil.parseClaims(token, jwtKey);
            Claims claims = jws.getPayload();

            return new AuthTokenResp(token, DateTimeUtil.ofDate(claims.getExpiration()));
        }

        token = JwtUtil.generate(Map.of("id", user.getId()), jwtExp, jwtKey);
        redis.string().set(String.format("%s_%s", user.getId(), token),
                JsonUtil.serializeAsString(Map.of("id", user.getId(),
                        "name", user.getName(),
                        "phone", user.getPhone())),
                jwtExp);
        return new AuthTokenResp(token, LocalDateTime.now().plusSeconds(jwtExp.toSeconds()));
    }

    @Override
    public Boolean revoke() {
        UserEntity user = AuthUtil.getUser();
        String token = user.getToken();
        return redis.delete(String.format("%s_%s", user.getId(), token));
    }

}
