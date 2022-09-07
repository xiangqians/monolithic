package org.monolithic.po.addl.user;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.monolithic.util.JacksonUtils;

import java.io.IOException;

/**
 * @author xiangqian
 * @date 22:23 2022/09/06
 */
@Data
public class UserAddlInfo {

    public String serialize() {
        try {
            return JacksonUtils.toJson(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static UserAddlInfo deserialize(String addlInfo) {
        if (StringUtils.isEmpty(addlInfo)) {
            return null;
        }
        try {
            return JacksonUtils.toObject(addlInfo, UserAddlInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
