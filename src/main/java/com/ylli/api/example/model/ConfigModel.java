package com.ylli.api.example.model;

import io.mybatis.provider.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity.Table("t_config")
@Data
@NoArgsConstructor
public class ConfigModel {

    @Entity.Column(id = true, updatable = false)
    public Long id;

    public String name;

    public String value;

    public String desc;

    public Timestamp createTime;

    public Timestamp updateTime;

    public ConfigModel(String name, String value, String desc) {
        this.name = name;
        this.value = value;
        this.desc = desc;
    }
}
