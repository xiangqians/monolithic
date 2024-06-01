package org.xiangqian.monolithic.biz.auth;

import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.Description;

/**
 * @author xiangqian
 * @date 16:49 2024/06/01
 */
public interface AuthCode extends Code {

    @Description("授权类型不能为空")
    String TYPE_NOT_EMPTY = "auth_" + "type_not_empty";

    @Description("授权类型不合法")
    String TYPE_ILLEGAL = "auth_" + "type_illegal";

    @Description("用户名不能为空")
    String NAME_NOT_EMPTY = "auth_" + "name_not_empty";

    @Description("密码不能为空")
    String PASSWD_NOT_EMPTY = "auth_" + "passwd_not_empty";

    @Description("手机号不能为空")
    String PHONE_NOT_EMPTY = "auth_" + "phone_not_empty";

    @Description("短信验证码不能为空")
    String SMS_CODE_NOT_EMPTY = "auth_" + "sms_code_not_empty";

    @Description("用户名或密码错误")
    String NAME_OR_PASSWD_INCORRECT = "auth_" + "name_or_passwd_incorrect";

    @Description("手机号或密码错误")
    String PHONE_OR_PASSWD_INCORRECT = "auth_" + "phone_or_passwd_incorrect";

    @Description("手机号或密码错误")
    String PHONE_OR_SMS_CODE_INCORRECT = "auth_" + "phone_or_sms_code_incorrect";

}
