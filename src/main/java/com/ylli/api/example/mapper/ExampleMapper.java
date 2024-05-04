package com.ylli.api.example.mapper;

import com.ylli.api.example.model.ExampleModel;
import io.mybatis.mapper.Mapper;
import io.mybatis.mapper.base.EntityProvider;
import io.mybatis.provider.Caching;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Options;

@org.apache.ibatis.annotations.Mapper
public interface ExampleMapper extends Mapper<ExampleModel, Long> {

    @Lang(Caching.class)
    //@SelectKey(statement = "SELECT SEQ.NEXTVAL FROM DUAL", keyProperty = "id", before = true, resultType = long.class)
    @Options(useGeneratedKeys = false, keyProperty = "id")
    @InsertProvider(type = EntityProvider.class, method = "insert")
    int insert(ExampleModel entity);

    @Lang(Caching.class)
    //@SelectKey(statement = "SELECT SEQ.NEXTVAL FROM DUAL", keyProperty = "id", before = true, resultType = long.class)
    @Options(useGeneratedKeys = false, keyProperty = "id")
    @InsertProvider(type = EntityProvider.class, method = "insertSelective")
    int insertSelective(ExampleModel entity);
}
