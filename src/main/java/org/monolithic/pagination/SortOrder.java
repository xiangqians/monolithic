package org.monolithic.pagination;

import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Set;

/**
 * 排序命令
 *
 * @author xiangqian
 * @date 23:15 2022/06/14
 */
public interface SortOrder {

    // 升序
    Integer ASCENDING = 1;

    // 降序
    Integer DESCENDING = 2;

    Set<Integer> SET = Set.of(1, 2);

    String API_MODEL_PROPERTY_VALUE = "排序命令，1-升序，2-降序";
    String API_MODEL_PROPERTY_ALLOWABLE_VALUES = "1,2";

    static void check(Integer sortOrder) {
        Assert.isTrue(Objects.nonNull(sortOrder) && SET.contains(sortOrder), String.format("排序命令不合法，%s", sortOrder));
    }

}
