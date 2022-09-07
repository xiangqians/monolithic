package org.monolithic.pagination;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.monolithic.o.VoParam;
import org.springframework.util.Assert;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 分页参数
 *
 * @author xiangqian
 * @date 14:41:59 2022/06/12
 */
@Data
@ApiModel(description = "分页参数信息")
public class PageRequest implements VoParam {

    protected static final String SORT_FIELDS_DOCUMENT_DESCRIPTION = "注：排序字段集，字段间以英文逗号（,）分隔";

    protected static final Map<String, String> SORTABLE_FIELD_MAP = Map.of("createTime", "create_time",
            "updateTime", "update_time");

    @Min(value = 1, message = "当前页必须大于0")
    @NotNull(message = "当前页不能为空")
    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Long current;

    @Min(value = 1, message = "页数量必须大于0")
    @NotNull(message = "页数量不能为空")
    @ApiModelProperty(value = "页数量", example = "10", required = true)
    private Long size;

    @ApiModelProperty(SORT_FIELDS_DOCUMENT_DESCRIPTION)
    private String sortFields;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private Set<String> sortFieldSet;

    @ApiModelProperty(value = SortOrder.API_MODEL_PROPERTY_VALUE, allowableValues = SortOrder.API_MODEL_PROPERTY_ALLOWABLE_VALUES)
    private Integer sortOrder;

    public PageRequest() {
        this(1L, 10L);
    }

    public PageRequest(long current, long size) {
        this.current = current;
        this.size = size;
    }

    /**
     * 可排序的字段映射
     *
     * @return
     */
    protected Map<String, String> sortableFieldMap() {
        return null;
    }

    @Override
    public void post() {
        // sortFields
        sortFields = StringUtils.trimToNull(sortFields);
        Map<String, String> sortableFieldMap = null;
        if (Objects.nonNull(sortFields) && Objects.nonNull(sortableFieldMap = sortableFieldMap())) {
            String[] sortFieldArray = sortFields.split(",");
            sortFieldSet = new HashSet<>(sortFieldArray.length);
            for (String sortField : sortFieldArray) {
                String value = sortableFieldMap.get(sortField);
                Assert.notNull(value, String.format("%s，此字段不支持排序！", sortField));
                sortFieldSet.add(value);
            }
        }

        // sortOrder
        if (Objects.nonNull(sortOrder)) {
            SortOrder.check(sortOrder);
        }

    }


}
