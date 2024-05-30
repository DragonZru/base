package com.ylli.api.example;

import com.ylli.api.common.exception.GenericException;
import com.ylli.api.example.model.ExampleModel;
import com.ylli.api.example.service.ExampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/example")
public class ExampleController {

    @Autowired
    ExampleService exampleService;

    @PostMapping
    public void create(@RequestBody ExampleModel model) {
        exampleService.create(model);
    }

    @DeleteMapping
    public void delete(@RequestParam Long id) {
        exampleService.delete(id);
    }

    /**
     * @param id        精准查询
     * @param username  模糊查询 like 'username%'
     * @param extras    str,str1,str2... any match 匹配任意元素即可
     * @param version   精准查询
     * @param status    精准查询
     * @param leftTime  >= leftTime
     * @param rightTime <= rightTime
     * @return
     */
    @GetMapping
    public List<ExampleModel> get(@RequestParam(required = false) Long id,
                                  @RequestParam(required = false) String username,
                                  @RequestParam(required = false) Long version,
                                  @RequestParam(required = false) Boolean status,
                                  @RequestParam(required = false) List<Object> extras,
                                  @RequestParam(required = false) Timestamp leftTime,
                                  @RequestParam(required = false) Timestamp rightTime,
                                  @RequestParam(required = false, defaultValue = "0") Integer offset,
                                  @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return exampleService.get(id, username, version, status, extras, leftTime, rightTime, offset, limit);
    }

    @PutMapping
    public void update(@RequestBody ExampleModel model) {
        if (model.id == null) {
            throw new GenericException(HttpStatus.BAD_REQUEST, "id is required");
        }
        exampleService.update(model);
    }
}
