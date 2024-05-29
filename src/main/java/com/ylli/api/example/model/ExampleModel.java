package com.ylli.api.example.model;

import com.ylli.api.common.uid.SnowFlakeGenerator;
import io.mybatis.provider.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Entity.Table("t_example")
@Data
@NoArgsConstructor
// Use a different name with mapper in Example.class
public class ExampleModel {

    @Entity.Column(id = true, updatable = false, genId = SnowFlakeGenerator.class)
    //@Entity.Column(id = true, updatable = false) = @Id 使用mysql自增
    public Long id;

    public String username;

    public String password;

    // 可以是任意基础类型，any java basic type
    public List<String> strings;

    public Long version;

    public Boolean status;

    // 排除列
    @Entity.Transient
    public Map<String, Object> map;

    public Timestamp createTime;

    public Timestamp updateTime;

    public ExampleModel(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
