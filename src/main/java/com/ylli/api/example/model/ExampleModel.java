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
    public Long id;

    public String username;

    public String password;

    // 可以是任意基础类型，any java basic type
    public List<Object> strings;

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

    /*
        CREATE TABLE `example`.`t_example`  (
      `id` bigint UNSIGNED NOT NULL,
      `username` varchar(255) NOT NULL,
      `password` varchar(255) NOT NULL,
      `version` bigint NOT NULL DEFAULT 0,
      `status` tinyint(1) NOT NULL DEFAULT 1,
      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
      `update_time` datetime NULL ON UPDATE CURRENT_TIMESTAMP,
      PRIMARY KEY (`id`)
    );
         */
}
