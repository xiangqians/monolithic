package org.xiangqian.monolithic.common.mybatis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author xiangqian
 * @date 22:33 2024/03/04
 */
@Data
@Schema(description = "延迟加载列表信息")
public class LazyList<T> {

    @Schema(description = "当前页", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer current;

    @Schema(description = "页数量", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer size;

    @Schema(description = "是否有下一页数据")
    private Boolean next;

    @Schema(description = "数据")
    private List<T> data;

    public LazyList() {
        this(1, 10);
    }

    public LazyList(Integer current, Integer size) {
        this.current = current;
        this.size = size;
        this.next = false;
        this.data = Collections.emptyList();
    }

}
