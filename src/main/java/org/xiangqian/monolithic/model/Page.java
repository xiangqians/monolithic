package org.xiangqian.monolithic.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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

    @Schema(description = "当前页", example = "1", required = true)
    private Long current;

    @Schema(description = "页数量", example = "10", required = true)
    private Long size;

    @Schema(description = "总页数")
    private Long pages;

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "数据")
    private List<T> data;

    public Page() {
        this(1L, 10L);
    }

    public Page(long current, long size) {
        this.current = current;
        this.size = size;
        this.pages = 0L;
        this.total = 0L;
        this.data = Collections.emptyList();
    }

    /**
     * page转换（T -> R）
     *
     * @param func
     * @param <R>
     * @return
     */
    public <R> Page<R> conv(Function<T, R> func) {
        Page<R> page = new Page<>();
        page.setCurrent(getCurrent());
        page.setSize(getSize());
        page.setPages(getPages());
        page.setTotal(getTotal());
        page.setData(Optional.ofNullable(getData()).map(data -> data.stream().map(func::apply).collect(Collectors.toList())).orElse(Collections.emptyList()));
        return page;
    }

}
