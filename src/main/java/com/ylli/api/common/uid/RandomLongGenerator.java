package com.ylli.api.common.uid;

import io.mybatis.provider.EntityColumn;
import io.mybatis.provider.EntityTable;
import io.mybatis.provider.keysql.GenId;

import java.util.random.RandomGenerator;

public class RandomLongGenerator implements GenId<Long>, IDGenerator<Long> {

    @Override
    public Long genId(EntityTable table, EntityColumn column) {
        return nextId(1, Long.MAX_VALUE);
    }

    @Override
    public Long nextId() {
        return nextId(1, Long.MAX_VALUE);
    }

    // java 17 Enhanced Pseudo-Random Number Generators
    public static Long nextId(long origin, long bound) {
        return RandomGenerator.getDefault().nextLong(origin, bound);
    }
}
