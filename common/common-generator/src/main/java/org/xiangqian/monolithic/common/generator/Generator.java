package org.xiangqian.monolithic.common.generator;

/**
 * 生成器
 *
 * @author xiangqian
 * @date 21:23 2024/07/16
 */
public interface Generator {

    /**
     * 生成模板
     *
     * @param basePkg    基础包
     * @param moduleName 模块名
     * @param author     作者
     * @param database   数据库名
     * @param tables     数据表集合
     */
    void generateTemplate(String basePkg, String moduleName, String author, String database, String... tables) throws Exception;

    /**
     * 生成 INSERT 语句
     *
     * @param database 数据库名
     * @param table    数据表
     * @param name     参数名
     * @throws Exception
     */
    void generateInsert(String database, String table, String name) throws Exception;

    /**
     * 生成 UPDATE 语句
     *
     * @param database 数据库名
     * @param table    数据表
     * @param name     参数名
     * @throws Exception
     */
    void generateUpdate(String database, String table, String name) throws Exception;

    /**
     * 生成 SELECT 语句
     *
     * @param database 数据库名
     * @param table    数据表
     * @param alias    数据表别名
     * @param name     参数名
     * @throws Exception
     */
    void generateSelect(String database, String table, String alias, String name) throws Exception;

}
