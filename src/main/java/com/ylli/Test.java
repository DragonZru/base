package com.ylli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class Test {

    @Autowired
    DataSource dataSource;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    void init() throws SQLException {

        Connection connection = dataSource.getConnection();

        System.out.println(connection.getMetaData().getURL());
    }
}
