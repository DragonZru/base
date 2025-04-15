package com.ylli.api.example.model;

import io.mybatis.provider.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity.Table("t_example")
@Data
@NoArgsConstructor
// Use a different name for Example.class, because it defined in mapper
//broadcast table
public class ExampleModel {

    @Entity.Column(id = true, updatable = false
//            , genId = SnowFlakeGenerator.class
    )
    //@Entity.Column(id = true, updatable = false) = @Id 使用mysql自增 or 自定义id 生成策略
    public Long id;

    public String username;

    public String password;

    public Long version;

    public Boolean status;

    // 可以是任意类型，any java basic type & reference type
    public List<ExampleInfo> extras;
    //public List<String> extras;

    // fullText search.
    public String value;

    public Timestamp createTime;

    public Timestamp updateTime;

    public ExampleModel(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
