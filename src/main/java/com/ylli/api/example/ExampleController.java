package com.ylli.api.example;

import com.ylli.api.common.uid.UUIDGenerator;
import com.ylli.api.example.mapper.ExampleMapper;
import com.ylli.api.example.model.ExampleModel;
import com.ylli.api.example.service.ExampleService;
import io.mybatis.mapper.example.Example;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class ExampleController {

    @Autowired
    ExampleService exampleService;

    @Autowired
    ExampleMapper exampleMapper;

    @Autowired
    ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void test() {
        ExampleModel exampleModel = new ExampleModel("ylli", "wsy");

        exampleModel.strings = Arrays.asList("1L", "2L", "3L");

        exampleMapper.insertSelective(exampleModel);

        Example<ExampleModel> example = new Example<>();
        example.createCriteria().andEqualTo(ExampleModel::getUsername, "ylli");

        List<ExampleModel> list = exampleMapper.selectByExample(example);
        System.out.println(list);

        exampleMapper.updateByExampleSelective(new ExampleModel("name", "description4"), example);
        exampleMapper.deleteByExample(example);

        System.out.println(new UUIDGenerator().genId(null, null));

        System.out.println(reactiveStringRedisTemplate.opsForValue().setIfAbsent("key", "val"));
        System.out.println(reactiveStringRedisTemplate.opsForValue().get("key").subscribe(System.out::println));

        System.out.println(stringRedisTemplate.opsForValue().setIfAbsent("key1", "val1"));
        System.out.println(stringRedisTemplate.opsForValue().get("key1"));
    }
}
