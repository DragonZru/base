package com.ylli.api.common.uid;

import io.mybatis.provider.EntityColumn;
import io.mybatis.provider.EntityTable;
import io.mybatis.provider.keysql.GenId;

import java.util.random.RandomGenerator;

public class UUIDGenerator implements GenId<Long>,IDGenerator<Long> {

    @Override
    public Long genId(EntityTable table, EntityColumn column) {
        return nextId();
    }

    @Override
    public Long nextId() {
        // java 17 Enhanced Pseudo-Random Number Generators
        return RandomGenerator.getDefault().nextLong(1, Long.MAX_VALUE);
    }
}
