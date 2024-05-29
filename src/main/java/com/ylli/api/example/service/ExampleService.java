package com.ylli.api.example.service;

import com.ylli.api.common.exception.GenericException;
import com.ylli.api.example.mapper.ExampleMapper;
import com.ylli.api.example.model.ExampleModel;
import io.mybatis.mapper.example.ExampleWrapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ExampleService {

    ExampleMapper exampleMapper;

    ExampleService(ExampleMapper exampleMapper) {
        this.exampleMapper = exampleMapper;
    }

    /**
     * insert          插入all fields
     * insertSelective 插入给定字段，其余使用mysql default
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(ExampleModel model) {
        if (exampleMapper.wrapper().eq(ExampleModel::getUsername, model.username).count() != 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "username already exists");
        }
        exampleMapper.insertSelective(model);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        exampleMapper.deleteByPrimaryKey(id);
    }

    public List<ExampleModel> get(Long id, String username, List<String> list, Long version, Boolean status,
                                  Timestamp leftTime, Timestamp rightTime, Integer offset, Integer limit) {
        ExampleWrapper<ExampleModel, Long> exampleWrapper = exampleMapper.wrapper();
        if (list != null) {
            //JSON_OVERLAPS(json_doc1, json_doc2)
            //Compares two JSON documents.
            //Returns true (1) if the two document have any key-value pairs or array elements in common.
            //返回包含list任意元素的集合
            //important!  <list 元素 str...  str,'str',"str" 被视为不同元素.故需要与数据库完全一致Exact Match>
            //eg. 如果exampleModel.string=["1","2","3"],那么这里传参需要"1","2"...,不带"",1,2视为不同元素故返回empty result
            exampleWrapper.anyCondition("JSON_OVERLAPS (strings, '" + list + "' )");
        }
        if (id != null) {
            exampleWrapper.eq(ExampleModel::getId, id);
        }
        if (username != null) {
            exampleWrapper.startsWith(ExampleModel::getUsername, username);
        }
        if (version != null) {
            exampleWrapper.eq(ExampleModel::getVersion, version);
        }
        if (status != null) {
            exampleWrapper.eq(ExampleModel::getStatus, status);
        }
        if (leftTime != null) {
            exampleWrapper.ge(ExampleModel::getCreateTime, leftTime);
        }
        if (rightTime != null) {
            exampleWrapper.le(ExampleModel::getCreateTime, rightTime);
        }
        return exampleMapper.selectByExample(exampleWrapper.example(), new RowBounds(offset, limit));
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(ExampleModel source) {
        Optional<ExampleModel> target = exampleMapper.selectByPrimaryKey(source.id);
        if (target.isEmpty()) {
            throw new GenericException(HttpStatus.BAD_REQUEST, String.format("id %s not exists", source.id));
        }
        copyPropertiesIgnoreNull(source, target.get());
        target.get().version = target.get().version + 1;
        target.get().updateTime = Timestamp.from(Instant.now());
        exampleMapper.updateByPrimaryKeySelective(target.get());
    }

    public void copyPropertiesIgnoreNull(Object source, Object target) {
        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        List<String> ignoreProperties = new ArrayList<>();
        Stream.of(source.getClass().getFields()).forEach(field -> {
            try {
                if (field.get(source) == null) {
                    ignoreProperties.add(field.getName());
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        BeanUtils.copyProperties(source, target, ignoreProperties.toArray(new String[0]));
    }
}
