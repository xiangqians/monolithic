package org.monolithic.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.monolithic.pagination.Page;
import org.monolithic.pagination.PageRequest;
import org.monolithic.pagination.SortOrder;
import org.monolithic.po.ComPo;

import java.util.*;

/**
 * @author xiangqian
 * @date 01:14 2022/07/21
 */
public interface DaoHelper {

    public static <T extends ComPo> void sort(LambdaQueryWrapper<T> lambdaQueryWrapper, PageRequest pageRequest) {
        Set<String> sortFieldSet = pageRequest.getSortFieldSet();
        Integer sortOrder = null;
        if (Objects.isNull(sortFieldSet) || Objects.isNull(sortOrder = pageRequest.getSortOrder())) {
            return;
        }

        List<SFunction<T, ?>> sFunctions = new ArrayList<>(sortFieldSet.size());
        for (String sortField : sortFieldSet) {
            if ("create_time".equals(sortField)) {
                sFunctions.add(T::getCreateTime);
            } else if ("update_time".equals(sortField)) {
                sFunctions.add(T::getUpdateTime);
            }
        }

        // 升序
        if (sortOrder == SortOrder.ASCENDING) {
            lambdaQueryWrapper.orderByAsc(sFunctions);
        }
        // 降序
        else if (sortOrder == SortOrder.DESCENDING) {
            lambdaQueryWrapper.orderByDesc(sFunctions);
        }
    }

    static <T> Page<T> queryForPage(BaseMapper<T> baseMapper, PageRequest pageRequest, Wrapper<T> queryWrapper) {
        return convertMybatisPageToPage(baseMapper.selectPage(createMybatisPage(pageRequest), queryWrapper));
    }

    static <T> Page<T> convertMybatisPageToPage(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        Page<T> tPage = new Page<>();
        tPage.setCurrent(page.getCurrent());
        tPage.setSize(page.getSize());
        tPage.setPages(page.getPages());
        tPage.setTotal(page.getTotal());
        tPage.setData(Optional.ofNullable(page.getRecords()).orElse(Collections.emptyList()));
        return tPage;
    }

    static <T> com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> createMybatisPage(PageRequest pageRequest) {
        return new com.baomidou.mybatisplus.extension.plugins.pagination.Page(pageRequest.getCurrent(), pageRequest.getSize());
    }

}
