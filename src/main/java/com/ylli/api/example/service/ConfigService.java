package com.ylli.api.example.service;

import com.ylli.api.common.exception.GenericException;
import com.ylli.api.example.mapper.ConfigMapper;
import com.ylli.api.example.model.ConfigModel;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class ConfigService {

    ConfigMapper configMapper;

    public ConfigService(ConfigMapper configMapper) {
        this.configMapper = configMapper;
    }


    @Transactional(rollbackFor = Exception.class)
    public void create(@NonNull String name, String value, String desc) {
        if (get(name) != null) {
            throw new GenericException(HttpStatus.BAD_REQUEST, String.format("key %s already exist", name));
        }
        ConfigModel config = new ConfigModel(name, value, desc);
        configMapper.insertConfig(config);
    }

    public ConfigModel get(String name) {
        return configMapper.selectByName(name);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        configMapper.deleteByPrimaryKey(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public ConfigModel update(ConfigModel config) {
        ConfigModel c = get(config.name);
        if (c == null) {
            throw new GenericException(HttpStatus.NOT_FOUND, String.format("config %s not found", config.name));
        }
        if (config.value != null) {
            c.value = config.value;
        }
        if (config.desc != null) {
            c.desc = config.desc;
        }
        c.updateTime = Timestamp.from(Instant.now());
        configMapper.updateByPrimaryKeySelective(c);
        return get(config.name);
    }
}
