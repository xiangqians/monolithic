package org.xiangqian.monolithic.common.biz.sys.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenEmailArg;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenPhoneArg;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenResult;
import org.xiangqian.monolithic.common.biz.sys.service.SecurityService;
import org.xiangqian.monolithic.common.biz.sys.service.UserService;
import org.xiangqian.monolithic.common.model.Assert;
import org.xiangqian.monolithic.common.model.CodeException;
import org.xiangqian.monolithic.common.mysql.sys.SysCode;
import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;
import org.xiangqian.monolithic.common.mysql.sys.mapper.UserMapper;
import org.xiangqian.monolithic.common.redis.Redis;
import org.xiangqian.monolithic.common.util.JsonUtil;
import org.xiangqian.monolithic.common.util.JwtUtil;
import org.xiangqian.monolithic.common.util.time.DateTimeUtil;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author xiangqian
 * @date 17:04 2024/06/01
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final SecretKey jwtKey;
    private final Duration jwtExp;

    @Autowired
    private Redis redis;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecurityService securityService;

    public UserServiceImpl(@Value("${jwt.key}") String jwtKey, @Value("${jwt.exp}") Duration jwtExp) {
        this.jwtKey = Keys.hmacShaKeyFor(jwtKey.getBytes());
        this.jwtExp = jwtExp;
    }

    @Override
    public UserTokenResult getTokenByEmail(UserTokenEmailArg arg) {
//        UserEntity user = null;
//
//        // 验证类型
//        byte type = arg.getType();
//
//        // 密码
//        if (type == 1) {
//            String email = StringUtils.trim(arg.getEmail());
//            Assert.notEmpty(email, SysCode.USER_EMAIL_NOT_EMPTY);
//            String passwd = StringUtils.trim(arg.getPasswd());
//            Assert.notEmpty(passwd, SysCode.USER_PASSWD_NOT_EMPTY);
//            user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
//                    .eq(UserEntity::getEmail, email)
//                    .last("LIMIT 1"));
//            Assert.notNull(user, SysCode.USER_EMAIL_OR_PASSWD_INCORRECT);
//            Assert.isTrue(passwordEncoder.matches(passwd, user.getPasswd()), SysCode.USER_EMAIL_OR_PASSWD_INCORRECT);
//        }
//        // 验证码
//        else if (type == 2) {
//            String email = StringUtils.trim(arg.getEmail());
//            Assert.notEmpty(email, SysCode.USER_EMAIL_NOT_EMPTY);
//            String code = StringUtils.trim(arg.getCode());
//            Assert.notEmpty(code, SysCode.USER_CODE_NOT_EMPTY);
//            throw new RuntimeException("暂不支持验证码授权");
//        }
//        // 验证类型不合法
//        else {
//            throw new CodeException(SysCode.USER_VERIFY_TYPE_ILLEGAL);
//        }
//
//        return getTokenByUser(user);

        return null;
    }

    @Override
    public UserTokenResult getTokenByPhone(UserTokenPhoneArg arg) {
//        UserEntity user = null;
//
//        // 验证类型
//        byte type = arg.getType();
//
//        // 密码
//        if (type == 1) {
//            String phone = StringUtils.trim(arg.getPhone());
//            Assert.notEmpty(phone, SysCode.USER_PHONE_NOT_EMPTY);
//            String passwd = StringUtils.trim(arg.getPasswd());
//            Assert.notEmpty(passwd, SysCode.USER_PASSWD_NOT_EMPTY);
//            user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
//                    .eq(UserEntity::getPhone, phone)
//                    .last("LIMIT 1"));
//            Assert.notNull(user, SysCode.USER_PHONE_OR_PASSWD_INCORRECT);
//            Assert.isTrue(passwordEncoder.matches(passwd, user.getPasswd()), SysCode.USER_PHONE_OR_PASSWD_INCORRECT);
//        }
//        // 验证码
//        else if (type == 2) {
//            String phone = StringUtils.trim(arg.getPhone());
//            Assert.notEmpty(phone, SysCode.USER_PHONE_NOT_EMPTY);
//            String code = StringUtils.trim(arg.getCode());
//            Assert.notEmpty(code, SysCode.USER_CODE_NOT_EMPTY);
//            throw new RuntimeException("暂不支持验证码授权");
//        }
//        // 验证类型不合法
//        else {
//            throw new CodeException(SysCode.USER_VERIFY_TYPE_ILLEGAL);
//        }
//
//        return getTokenByUser(user);

        return null;
    }

    @Override
    public UserEntity getByToken(String token) {
        try {
            Jws<Claims> jws = JwtUtil.parseClaims(token, jwtKey);
            Object id = jws.getPayload().get("id");
            Object value = redis.String().get(getTokenKey(id));
            if (value != null) {
                return JsonUtil.deserialize(value.toString(), UserEntity.class);
            }
        } catch (Exception e) {
            log.warn("", e);
        }
        return null;
    }

    @Override
    public Boolean revokeToken() {
        UserEntity user = securityService.getUser();
        return redis.delete(getTokenKey(user.getId()));
    }

    @SneakyThrows
    private UserTokenResult getTokenByUser(UserEntity user) {
        String token = null;

        Object value = redis.String().get(getTokenKey(user.getId()));
        if (value != null) {
            user = JsonUtil.deserialize(value.toString(), UserEntity.class);
            token = user.getToken();

            Jws<Claims> jws = JwtUtil.parseClaims(token, jwtKey);
            Claims claims = jws.getPayload();

            return new UserTokenResult(token, DateTimeUtil.ofDate(claims.getExpiration()));
        }

        token = JwtUtil.generate(Map.of("id", user.getId()), jwtExp, jwtKey);
        redis.String().set(getTokenKey(user.getId()),
                JsonUtil.serializeAsString(Map.of("id", user.getId(),
                        "tenantId", user.getTenantId(),
                        "roleId", user.getRoleId(),
                        "name", user.getName(),
                        "email", user.getEmail(),
                        "phone", user.getPhone(),
                        "token", token)),
                jwtExp.minus(Duration.ofSeconds(30)));
        return new UserTokenResult(token, LocalDateTime.now().plusSeconds(jwtExp.toSeconds()));
    }

    private String getTokenKey(Object userId) {
        return "biz_sys_user_" + userId;
    }

}
