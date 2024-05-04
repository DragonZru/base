package com.ylli.api.config.mybatis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@MappedTypes(value = {List.class})
public class ListTypeHandler extends BaseTypeHandler<List> {

    static Gson gson = new GsonBuilder().create();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, gson.toJson(parameter));
    }

    @Override
    public List getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.wasNull() ? null : gson.fromJson(rs.getString(columnName), List.class);
    }

    @Override
    public List getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.wasNull() ? null : gson.fromJson(rs.getString(columnIndex), List.class);
    }

    @Override
    public List getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.wasNull() ? null : gson.fromJson(cs.getString(columnIndex), List.class);
    }
}
