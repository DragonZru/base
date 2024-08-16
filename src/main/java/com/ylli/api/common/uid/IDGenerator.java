package com.ylli.api.common.uid;

import io.mybatis.provider.EntityColumn;
import io.mybatis.provider.EntityTable;
import io.mybatis.provider.keysql.GenId;

public interface IDGenerator<T> extends GenId<T> {
    //确保每次调用都返回唯一id
    T next();

    /*
     * io.mybatis.mapper 插件 ID 生成器
     */
    default T genId(EntityTable table, EntityColumn column) {
        return null;
    }
}
