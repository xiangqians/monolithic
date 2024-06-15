package org.xiangqian.monolithic.biz.sys.service.impl;

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
import org.xiangqian.monolithic.biz.sys.SysCode;
import org.xiangqian.monolithic.biz.sys.entity.UserEntity;
import org.xiangqian.monolithic.biz.sys.mapper.UserMapper;
import org.xiangqian.monolithic.biz.sys.model.UserTokenEmailArg;
import org.xiangqian.monolithic.biz.sys.model.UserTokenPhoneArg;
import org.xiangqian.monolithic.biz.sys.model.UserTokenResult;
import org.xiangqian.monolithic.biz.sys.service.UserService;
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
public class UserServiceImpl implements UserService {

    private SecretKey jwtKey;
    private Duration jwtExp;

    @Autowired
    private Redis redis;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ThreadLocal<UserEntity> threadLocal;

    public UserServiceImpl(@Value("${jwt.key}") String jwtKey, @Value("${jwt.exp}") Duration jwtExp) {
        this.jwtKey = Keys.hmacShaKeyFor(jwtKey.getBytes());
        this.jwtExp = jwtExp;

        this.threadLocal = new ThreadLocal<>();
    }

    @Override
    public UserTokenResult getTokenByEmail(UserTokenEmailArg arg) {
        UserEntity user = null;

        // 验证类型
        byte type = arg.getType();

        // 密码
        if (type == 1) {
            String email = StringUtils.trim(arg.getEmail());
            Assert.notEmpty(email, SysCode.USER_EMAIL_NOT_EMPTY);
            String passwd = StringUtils.trim(arg.getPasswd());
            Assert.notEmpty(passwd, SysCode.USER_PASSWD_NOT_EMPTY);
            user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getEmail, email)
                    .last("LIMIT 1"));
            Assert.notNull(user, SysCode.USER_EMAIL_OR_PASSWD_INCORRECT);
            Assert.isTrue(passwordEncoder.matches(passwd, user.getPasswd()), SysCode.USER_EMAIL_OR_PASSWD_INCORRECT);
        }
        // 验证码
        else if (type == 2) {
            String email = StringUtils.trim(arg.getEmail());
            Assert.notEmpty(email, SysCode.USER_EMAIL_NOT_EMPTY);
            String code = StringUtils.trim(arg.getCode());
            Assert.notEmpty(code, SysCode.USER_CODE_NOT_EMPTY);
            throw new RuntimeException("暂不支持验证码授权");
        }
        // 验证类型不合法
        else {
            throw new CodeException(SysCode.USER_VERIFY_TYPE_ILLEGAL);
        }

        return getTokenByUser(user);
    }

    @Override
    public UserTokenResult getTokenByPhone(UserTokenPhoneArg arg) {
        UserEntity user = null;

        // 验证类型
        byte type = arg.getType();

        // 密码
        if (type == 1) {
            String phone = StringUtils.trim(arg.getPhone());
            Assert.notEmpty(phone, SysCode.USER_PHONE_NOT_EMPTY);
            String passwd = StringUtils.trim(arg.getPasswd());
            Assert.notEmpty(passwd, SysCode.USER_PASSWD_NOT_EMPTY);
            user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getPhone, phone)
                    .last("LIMIT 1"));
            Assert.notNull(user, SysCode.USER_PHONE_OR_PASSWD_INCORRECT);
            Assert.isTrue(passwordEncoder.matches(passwd, user.getPasswd()), SysCode.USER_PHONE_OR_PASSWD_INCORRECT);
        }
        // 验证码
        else if (type == 2) {
            String phone = StringUtils.trim(arg.getPhone());
            Assert.notEmpty(phone, SysCode.USER_PHONE_NOT_EMPTY);
            String code = StringUtils.trim(arg.getCode());
            Assert.notEmpty(code, SysCode.USER_CODE_NOT_EMPTY);
            throw new RuntimeException("暂不支持验证码授权");
        }
        // 验证类型不合法
        else {
            throw new CodeException(SysCode.USER_VERIFY_TYPE_ILLEGAL);
        }

        return getTokenByUser(user);
    }

    @Override
    public UserEntity getByToken(String token) {
        try {
            Jws<Claims> jws = JwtUtil.parseClaims(token, jwtKey);
            Object id = jws.getPayload().get("id");
            Object value = redis.String().get(getTokenKey(id, token));
            if (value != null) {
                UserEntity user = JsonUtil.deserialize(value.toString(), UserEntity.class);
                user.setToken(token);
                return user;
            }
        } catch (Exception e) {
            log.warn("", e);
        }
        return null;
    }

    @Override
    public UserEntity get() {
        return threadLocal.get();
    }

    @Override
    public void setUser(UserEntity user) {
        threadLocal.set(user);
    }

    @Override
    public Boolean revokeToken() {
        UserEntity user = get();
        String token = user.getToken();
        return redis.delete(getTokenKey(user.getId(), token));
    }

    @SneakyThrows
    private UserTokenResult getTokenByUser(UserEntity user) {
        String token = null;
        String prefix = getTokenKeyPrefix(user.getId());
        Set<String> keys = redis.keyWithPrefix(prefix, 1);
        if (CollectionUtils.isNotEmpty(keys)) {
            token = keys.iterator().next();
            token = token.substring(prefix.length());

            Jws<Claims> jws = JwtUtil.parseClaims(token, jwtKey);
            Claims claims = jws.getPayload();

            return new UserTokenResult(token, DateTimeUtil.ofDate(claims.getExpiration()));
        }

        token = JwtUtil.generate(Map.of("id", user.getId()), jwtExp, jwtKey);
        redis.String().set(getTokenKey(user.getId(), token),
                JsonUtil.serializeAsString(Map.of("id", user.getId(),
                        "tenantId", user.getTenantId(),
                        "roleId", user.getRoleId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "phone", user.getPhone())),
                jwtExp.minus(Duration.ofSeconds(30)));
        return new UserTokenResult(token, LocalDateTime.now().plusSeconds(jwtExp.toSeconds()));
    }

    private String getTokenKey(Object userId, Object token) {
        return getTokenKeyPrefix(userId) + token;
    }

    private String getTokenKeyPrefix(Object userId) {
        return "biz_sys_user_" + userId + "_";
    }

}
