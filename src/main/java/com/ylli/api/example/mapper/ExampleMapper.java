package com.ylli.api.example.mapper;

import com.ylli.api.example.model.ExampleModel;
import io.mybatis.mapper.BaseMapper;
import io.mybatis.mapper.list.ListMapper;

/**
 * BaseMapper
 * - EntityMapper
 * - ExampleMapper
 * - CursorMapper
 * FnMapper
 * ListMapper
 * LogicalMapper
 * - BaseMapper
 * - FnMapper
 */
@org.apache.ibatis.annotations.Mapper
public interface ExampleMapper extends BaseMapper<ExampleModel, Long>, ListMapper<ExampleModel> {

/*
    //主键不为id时，可以通过keyProperty指定，useGeneratedKeys使用JDBC自动生成主键（mysql auto_increment）
    @Override
    @Lang(Caching.class)
    @Options(useGeneratedKeys = true, keyProperty = "example_id")
    @InsertProvider(type = EntityProvider.class, method = "insert")
    int insert(ExampleModel entity);*/
}
