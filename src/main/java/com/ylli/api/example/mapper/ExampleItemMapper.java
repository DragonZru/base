package com.ylli.api.example.mapper;

import com.ylli.api.example.model.ExampleItem;
import io.mybatis.mapper.BaseMapper;
import io.mybatis.mapper.list.ListMapper;

@org.apache.ibatis.annotations.Mapper
public interface ExampleItemMapper extends BaseMapper<ExampleItem, Long>, ListMapper<ExampleItem> {
}
