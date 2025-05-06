package com.ylli.base.configuration.mybatis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ylli.base.api.example.model.ExampleInfo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 通用的 JSON 类型处理器, 可以绑定多个json类型
 * @author ylli
 * @param <T> 要处理的 Java 类型
 */
//@MappedTypes({ExampleInfo.class, User.class})
@MappedTypes({ExampleInfo.class})
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private static final Gson GSON = new GsonBuilder().create();
    private final Class<T> type;

    public JsonTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        try {
            String json = GSON.toJson(parameter);
            ps.setString(i, json);
        } catch (Exception e) {
            throw new SQLException("Error converting object to JSON", e);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return parseJson(json);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return parseJson(json);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return parseJson(json);
    }

    private T parseJson(String json) throws SQLException {
        if (json == null) {
            return null;
        }
        try {
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            throw new SQLException("Error converting JSON to object", e);
        }
    }
}
