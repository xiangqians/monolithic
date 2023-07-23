package org.xiangqian.monolithic.sys.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.xiangqian.monolithic.validation.constraint.Nothing;
import org.xiangqian.monolithic.validation.group.Add;
import org.xiangqian.monolithic.validation.group.Del;
import org.xiangqian.monolithic.validation.group.Upd;

/**
 * @author xiangqian
 * @date 21:34 2023/03/28
 */
@Data
@Schema(description = "test")
public class TestEntity {

    @NotNull(message = "id不能为空", groups = {Add.class})
    @Schema(description = "主键id", example = "1")
    private Long id;

    @Length(max = 2, message = "名称过长", groups = {Del.class, Upd.class})
    @Schema(description = "名称")
    private String name;

    @NotNull(message = "field1不能为空", groups = {Del.class, Upd.class})
    @Length(max = 2, message = "field1过长")
    @Schema(description = "field1")
    private String field1;

    @NotNull(message = "field2不能为空", groups = {Del.class})
    @Length(max = 2, message = "field2过长")
    @Schema(description = "field2")
    private String field2;

    @NotNull(message = "field3不能为空", groups = {Upd.class})
    @NotBlank(message = "field3不能为空白", groups = {Upd.class})
    @Length(max = 2, message = "field3过长")
    @Schema(description = "field3")
    private String field3;

    @NotNull(message = "field4不能为空")
    @Length(max = 2, message = "field4过长")
    @Schema(description = "field4")
    private String field4;

    @NotBlank(message = "不能为空白", groups = Add.class)
    @Nothing(groups = Add.class)
    @Schema(description = "field5")
    private String field5;

}
