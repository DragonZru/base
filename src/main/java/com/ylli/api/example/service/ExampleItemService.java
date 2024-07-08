package com.ylli.api.example.service;

import com.github.pagehelper.PageHelper;
import com.ylli.api.common.uid.RandomLongGenerator;
import com.ylli.api.example.mapper.ExampleItemMapper;
import com.ylli.api.example.model.ExampleItem;
import org.springframework.stereotype.Service;

@Service
public class ExampleItemService {

    ExampleItemMapper exampleItemMapper;

    public ExampleItemService(ExampleItemMapper exampleItemMapper) {
        this.exampleItemMapper = exampleItemMapper;
    }

    public void create() {
        ExampleItem exampleItem = new ExampleItem(2L);
        exampleItemMapper.insertSelective(exampleItem);
    }


    public Object get() {
        PageHelper.offsetPage(0, 1);
        return exampleItemMapper.selectByExample(exampleItemMapper.example());
    }
}
