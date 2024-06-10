package org.xiangqian.monolithic.util.redis;

import org.springframework.data.redis.core.ListOperations;

import java.util.List;

/**
 * Redis数据类型之List（列表）
 * 有序的字符串列表，支持头部和尾部的插入、删除操作，可以实现队列、栈等数据结构。
 * <p>
 * 使用场景：发布订阅等
 *
 * @author xiangqian
 * @date 12:12 2024/06/10
 */
public class RedisList {

    private ListOperations<String, Object> listOperations;
    private String key;

    public RedisList(ListOperations<String, Object> listOperations, String key) {
        this.listOperations = listOperations;
        this.key = key;
    }

    /**
     * 在列表的左侧插入元素
     *
     * @param value 元素值
     * @return
     */
    public Long leftPush(Object value) {
        return listOperations.leftPush(key, value);
    }

    /**
     * 在指定元素前插入新元素
     *
     * @param newValue
     * @param existingValue
     * @return
     */
    public Long leftPush(Object newValue, Object existingValue) {
        return listOperations.leftPush(key, newValue, existingValue);
    }

    /**
     * 在列表的右侧插入元素
     *
     * @param value 元素值
     * @return
     */
    public Long rightPush(Object value) {
        return listOperations.rightPush(key, value);
    }

    /**
     * 在指定元素后插入新元素
     *
     * @param newValue
     * @param existingValue
     * @return
     */
    public Long rightPush(Object newValue, Object existingValue) {
        return listOperations.rightPush(key, newValue, existingValue);
    }

    /**
     * 获取列表指定索引位置的元素
     *
     * @param index 索引值，从0开始
     * @return
     */
    public Object index(long index) {
        return listOperations.index(key, index);
    }

    /**
     * 弹出列表的左侧元素
     *
     * @return
     */
    public Object leftPop() {
        return listOperations.leftPop(key);
    }

    /**
     * 弹出列表的右侧元素
     *
     * @return
     */
    public Object rightPop() {
        return listOperations.rightPop(key);
    }

    /**
     * 获取指定范围内的元素集合
     *
     * @param start
     * @param end
     * @return
     */
    public List<Object> range(long start, long end) {
        return listOperations.range(key, start, end);
    }

    /**
     * 移除列表中值为value的元素
     *
     * @param value
     * @return removedCount
     */
    public Object remove(Object value) {
        return listOperations.remove(key, 0, value);
    }

    /**
     * 截取列表，保留指定范围内的元素
     *
     * @param start
     * @param end
     */
    public void trim(long start, long end) {
        listOperations.trim(key, start, end);
    }

    /**
     * 获取列表大小
     *
     * @return
     */
    public Long size() {
        return listOperations.size(key);
    }

}
