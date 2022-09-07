package org.monolithic.service;

import com.baomidou.mybatisplus.annotation.TableField;
import org.monolithic.vo.perm.PermVo;

import java.util.List;

/**
 * 权限表 服务类
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
public interface PermService {

    PermVo queryByPath(String path);

}
