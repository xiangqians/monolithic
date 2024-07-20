package org.xiangqian.monolithic.common.mybatis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 00:53 2022/06/12
 */
@Data
@Schema(description = "分页信息")
public class Page<T> {

    @Schema(description = "当前页", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer current;

    @Schema(description = "页数量", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer size;

    @Schema(description = "总数")
    private Integer total;

    @Schema(description = "数据")
    private List<T> data;

    public Page() {
        this(1, 10);
    }

    public Page(Integer current, Integer size) {
        this.current = current;
        this.size = size;
        this.total = 0;
        this.data = Collections.emptyList();
    }

    /**
     * page类型转换（T -> R）
     *
     * @param function
     * @param <R>
     * @return
     */
    public <R> Page<R> conv(Function<T, R> function) {
        Page<R> page = new Page<>();
        page.setCurrent(getCurrent());
        page.setSize(getSize());
        page.setTotal(getTotal());
        page.setData(Optional.ofNullable(getData())
                .filter(CollectionUtils::isNotEmpty)
                .map(data -> data.stream().map(function::apply).collect(Collectors.toList()))
                .orElse(Collections.emptyList()));
        return page;
    }

}
