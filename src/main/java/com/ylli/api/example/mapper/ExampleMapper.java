package com.ylli.api.example.mapper;

import com.ylli.api.example.model.ExampleModel;
import io.mybatis.mapper.BaseMapper;

@org.apache.ibatis.annotations.Mapper
public interface ExampleMapper extends BaseMapper<ExampleModel, Long> {
}
