package com.ylli.base.feign;

import com.ylli.base.api.example.model.ExampleModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author ylli
 */
@FeignClient(name = "base-test", path = "/seata")
public interface ExampleFeignClient {

    @PostMapping("/example")
    void createExample(@RequestBody ExampleModel model);
}
