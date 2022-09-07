package org.monolithic.pagination;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("分页信息")
public class Page<T> {

    @ApiModelProperty("当前页")
    private Long current;

    @ApiModelProperty("当前页数量")
    private Long size;

    @ApiModelProperty("总页数")
    private Long pages;

    @ApiModelProperty("总数")
    private Long total;

    @ApiModelProperty("数据")
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

    public <R> Page<R> convert(Function<T, R> function) {
        Page<R> page = new Page<>();
        page.setCurrent(getCurrent());
        page.setSize(getSize());
        page.setPages(getPages());
        page.setTotal(getTotal());
        page.setData(Optional.ofNullable(getData()).map(data -> data.stream().map(function::apply).collect(Collectors.toList())).orElse(Collections.emptyList()));
        return page;
    }

}
