package com.ylli.api.example;

import com.ylli.api.example.model.ExampleModel;
import com.ylli.api.example.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
public class ExampleController {

    @Autowired
    ExampleService exampleService;

    @PostMapping
    public Object Create(@RequestBody ExampleModel model) {
        return exampleService.Create(model);
    }
}
