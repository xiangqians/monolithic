package org.monolithic.constant;

import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Set;

/**
 * @author xiangqian
 * @date 22:23 2022/09/07
 */
public interface LockFlag {

    // 正常
    Integer NORMAL = 1;

    // 已锁定
    Integer LOCKED = 2;

    Set<Integer> SET = Set.of(NORMAL, LOCKED);

    String API_MODEL_PROPERTY_VALUE = "锁定标记，0-正常，1-已锁定";
    String API_MODEL_PROPERTY_ALLOWABLE_VALUES = "0,1";

    static void check(Integer lockFlag) {
        Assert.isTrue(Objects.nonNull(lockFlag) && SET.contains(lockFlag), String.format("锁定标记不合法，%s", lockFlag));
    }

}
