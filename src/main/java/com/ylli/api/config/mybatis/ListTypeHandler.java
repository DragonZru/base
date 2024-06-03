package com.ylli.api.config.mybatis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ListTypeHandler<T> extends BaseTypeHandler<List<T>> {

    static Gson gson = new GsonBuilder()
            //若想范型完全保持驼峰转下划线规范，需将此类定义成abstract父类,并重写specificType(),否则解析时会按数据库实际存储显示
            //直接导致需要额外重新定义每一个基本类型，太麻烦了.
            //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, gson.toJson(parameter));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.wasNull() ? null : gson.fromJson(rs.getString(columnName), specificType());
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.wasNull() ? null : gson.fromJson(rs.getString(columnIndex), specificType());
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.wasNull() ? null : gson.fromJson(cs.getString(columnIndex), specificType());
    }

    public TypeToken<List<T>> specificType() {
        return new TypeToken<List<T>>() {
        };
    }
}
