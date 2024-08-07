package org.xiangqian.monolithic.common.mysql.sys.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.xiangqian.monolithic.common.util.time.DateTimeUtil;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author xiangqian
 * @date 22:18 2024/05/30
 */
@Data
@Schema(description = "用户信息")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "租户id")
    private Long tenantId;

    @Schema(description = "角色id")
    private Long roleId;

    @Schema(description = "用户名")
    private String name;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "密码")
    private String passwd;

    @Schema(description = "是否已锁定，0-否，1-是")
    private Byte locked;

    @Schema(description = "用户连续错误登陆次数，超过3次则锁定用户")
    private Byte denyCount;

    // [{"ip": "localhost", "time": "2024/05/30 21:55:18"}]
    @Schema(description = "登录历史")
    private String loginHistory;

    @Schema(description = "是否已删除，0-未删除，1-已删除")
    private Byte del;

    @Schema(description = "创建时间")
    private LocalDateTime addTime;

    @Schema(description = "修改时间")
    private LocalDateTime updTime;

    @Schema(hidden = true)
    private String token;

    @JsonIgnore // 序列化时忽略该属性
    public String getToken() {
        return token;
    }

    @JsonProperty // 反序列化时包含该属性
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * 是否是系统管理员角色
     *
     * @return
     */
    public boolean isAdminRole() {
        return id != null && id.longValue() == 1;
    }

    /**
     * 是否未被锁定
     *
     * @return
     */
    public boolean isNonLocked() {
        return locked == 0;
    }

    /**
     * 是否未被限时锁定
     *
     * @return
     */
    public boolean isNonLimitedTimeLocked() {
        // 连续输错密码小于3次
        return denyCount < 3
                // 锁定24小时
                || Duration.ofSeconds(DateTimeUtil.toSecond(LocalDateTime.now()) - DateTimeUtil.toSecond(updTime)).toHours() >= 24;
    }

    /**
     * 获取已被限时锁定时间（单位s）
     *
     * @return
     */
    public long getLimitedTimeLockedTime() {
        return Duration.ofHours(24).toSeconds() - (DateTimeUtil.toSecond(LocalDateTime.now()) - DateTimeUtil.toSecond(updTime));
    }

}
