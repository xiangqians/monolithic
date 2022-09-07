package org.monolithic.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import org.monolithic.constant.DelFlag;
import org.monolithic.o.Po;
import org.monolithic.o.Vo;
import org.monolithic.vo.com.ComVo;

import java.time.LocalDateTime;

/**
 * @author xiangqian
 * @date 22:16 2022/08/16
 */
@Data
public abstract class ComPo implements Po {

    /**
     * 描述
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 删除标识，0-正常，1-已删除
     * {@link DelFlag}
     */
    @TableLogic
    private String delFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    @Override
    public <T extends Vo> T convertToVo(Class<T> type) {

        if (ComVo.class.isAssignableFrom(type)) {
            ComVo vo = null;
            try {
                vo = (ComVo) type.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            vo.setDesc(getDesc());
            vo.setCreateTime(getCreateTime());
            vo.setUpdateTime(getUpdateTime());
            return (T) vo;
        }

        return Po.super.convertToVo(type);
    }

}
