package org.xiangqian.monolithic.biz.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.biz.Assert;
import org.xiangqian.monolithic.biz.CodeException;
import org.xiangqian.monolithic.biz.Redis;
import org.xiangqian.monolithic.biz.auth.AuthCode;
import org.xiangqian.monolithic.biz.auth.service.AuthService;
import org.xiangqian.monolithic.biz.auth.vo.AuthRequest;
import org.xiangqian.monolithic.biz.sys.entity.UserEntity;
import org.xiangqian.monolithic.biz.sys.mapper.UserMapper;

/**
 * @author xiangqian
 * @date 17:04 2024/06/01
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private Redis redis;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String token(AuthRequest authRequest) {
        // 授权类型
        byte type = authRequest.getType();
        // 用户名/密码
        if (type == 1) {
            String name = StringUtils.trim(authRequest.getNop());
            Assert.notEmpty(name, AuthCode.NAME_NOT_EMPTY);
            String passwd = StringUtils.trim(authRequest.getPoc());
            Assert.notEmpty(passwd, AuthCode.PASSWD_NOT_EMPTY);

            UserEntity userEntity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getName, name)
                    .last("LIMIT 1"));
            Assert.notNull(userEntity, AuthCode.NAME_OR_PASSWD_INCORRECT);
            Assert.isTrue(passwordEncoder.matches(passwd, userEntity.getPasswd()), AuthCode.NAME_OR_PASSWD_INCORRECT);

            String token = userEntity.getId().toString();
            redis.string().set(token, true);

            return token;
        }
        // 手机号/密码
        else if (type == 2) {
            String phone = StringUtils.trim(authRequest.getNop());
            Assert.notEmpty(phone, AuthCode.PHONE_NOT_EMPTY);
            String passwd = StringUtils.trim(authRequest.getPoc());
            Assert.notEmpty(passwd, AuthCode.PASSWD_NOT_EMPTY);

            UserEntity userEntity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
                    .eq(UserEntity::getPhone, phone)
                    .last("LIMIT 1"));

        }
        // 手机号/短信验证码
        else if (type == 3) {
            String phone = StringUtils.trim(authRequest.getNop());
            Assert.notEmpty(phone, AuthCode.PHONE_NOT_EMPTY);
            String smsCode = StringUtils.trim(authRequest.getPoc());
            Assert.notEmpty(smsCode, AuthCode.SMS_CODE_NOT_EMPTY);
        }
        // ?
        else {
            throw new CodeException(AuthCode.TYPE_ILLEGAL);
        }

        return null;
    }

    @Override
    public Boolean revoke() {
        return null;
    }

}
