package com.ylli.api.example.service;

import com.ylli.api.common.exception.GenericException;
import com.ylli.api.example.mapper.ExampleMapper;
import com.ylli.api.example.model.ExampleModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExampleService {

    ExampleMapper exampleMapper;

    ExampleService(ExampleMapper exampleMapper) {
        this.exampleMapper = exampleMapper;
    }

    @Transactional
    public Object Create(ExampleModel model) {
        if (exampleMapper.wrapper().eq(ExampleModel::getUsername, model.username).count() != 0) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "username already exists");
        }
        return exampleMapper.insertSelective(model);
    }
}
