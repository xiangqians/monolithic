package org.monolithic.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.monolithic.o.Vo;

/**
 * 客户端详情
 *
 * @author xiangqian
 * @date 23:06 2022/09/06
 */
@Data
@TableName("oauth_client_details")
public class AuthClientDetailsPo extends ComPo {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端id
     */
    @TableId(value = "client_id", type = IdType.INPUT)
    private String clientId;

    /**
     * 资源ids
     */
    private String resourceIds;

    /**
     * 密钥是否必须，0-不必须；1-必须
     */
    private String secretRequired;

    /**
     * 客户端密码，client_secret字段不能直接是 secret 的原始值，需要经过加密
     */
    private String clientSecret;

    /**
     * 该客户端允许授权的范围，定义客户端的权限，这里只是一个标识，资源服务可以根据这个权限进行鉴权
     * all, read, write, trust
     */
    private String scope;

    /**
     * 该客户端允许授权的类型
     */
    private String authorizedGrantTypes;

    /**
     * 跳转的uri
     */
    private String registeredRedirectUris;

    /**
     * authorities
     */
    private String authorities;

    /**
     * 访问令牌有效期，单位：s
     * 令牌默认有效期2小时（60 * 60 * 2 s）
     */
    private Integer accessTokenValidity;

    /**
     * 刷新令牌有效期，单位：s
     * 刷新令牌默认有效期3天（60 * 60 * 24 * 3 s）
     */
    private Integer refreshTokenValidity;

    /**
     * 0-跳转到授权页面；1-不跳转，直接发令牌
     */
    private String autoApprove;

    /**
     * 附加信息
     */
    private String addlInfo;

    @Override
    public <T extends Vo> T convertToVo(Class<T> type) {
        return super.convertToVo(type);
    }

}
