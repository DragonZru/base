package com.ylli.api.example.model;

import io.mybatis.provider.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity.Table("t_example")
@Data
@NoArgsConstructor
public class Example {

    @Entity.Column(id = true, updatable = false)
    public Long id;

    public String name;

    public String description;

    public List<String> strings;

    public List<Long> ids;

    public Timestamp createTime;

    public Timestamp updateTime;

    public Example(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
