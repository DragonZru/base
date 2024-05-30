package com.ylli.api.example.model;

import com.ylli.api.common.uid.SnowFlakeGenerator;
import io.mybatis.provider.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

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

    public Long version;

    public Boolean status;

    // 可以是任意类型，any java basic type & reference type
    public List<Object> extras;

    public Timestamp createTime;

    public Timestamp updateTime;

    public ExampleModel(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
