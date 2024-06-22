package org.xiangqian.monolithic.common.db.sys;

import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.Description;

/**
 * @author xiangqian
 * @date 14:48 2024/06/01
 */
public interface SysCode extends Code {

    @Description("验证类型不能为空")
    String USER_VERIFY_TYPE_NOT_EMPTY = "sys_" + "user_verify_type_not_empty";

    @Description("验证类型不合法")
    String USER_VERIFY_TYPE_ILLEGAL = "sys_" + "user_verify_type_illegal";

    @Description("邮箱不能为空")
    String USER_EMAIL_NOT_EMPTY = "sys_" + "user_email_not_empty";

    @Description("手机号不能为空")
    String USER_PHONE_NOT_EMPTY = "sys_" + "user_phone_not_empty";

    @Description("密码不能为空")
    String USER_PASSWD_NOT_EMPTY = "sys_" + "user_passwd_not_empty";

    @Description("验证码不能为空")
    String USER_CODE_NOT_EMPTY = "sys_" + "user_code_not_empty";

    @Description("邮箱或密码错误")
    String USER_EMAIL_OR_PASSWD_INCORRECT = "sys_" + "user_email_or_passwd_incorrect";

    @Description("手机号或密码错误")
    String USER_PHONE_OR_PASSWD_INCORRECT = "sys_" + "user_phone_or_passwd_incorrect";

    @Description("验证码错误")
    String USER_CODE_INCORRECT = "sys_" + "user_code_incorrect";

}
