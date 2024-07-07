package com.ylli.api.example.service;

import com.ylli.api.common.uid.RandomLongGenerator;
import com.ylli.api.example.mapper.ExampleItemMapper;
import org.springframework.stereotype.Service;

@Service
public class ExampleItemService {

    ExampleItemMapper exampleItemMapper;

    public ExampleItemService(ExampleItemMapper exampleItemMapper) {
        this.exampleItemMapper = exampleItemMapper;
    }

    public void create() {
//        RandomLongGenerator.nextId(0,1);
    }
}
