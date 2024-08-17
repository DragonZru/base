package com.ylli.api.example.mapper;

import com.ylli.api.example.model.ConfigModel;
import io.mybatis.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@org.apache.ibatis.annotations.Mapper
public interface ConfigMapper extends BaseMapper<ConfigModel, Long> {

    @Select("SELECT * FROM t_config WHERE name = #{name} ")
    ConfigModel selectByName(@Param("name") String name);
}
