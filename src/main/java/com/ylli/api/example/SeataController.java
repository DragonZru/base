package com.ylli.api.example;

import com.ylli.api.example.model.ConfigModel;
import com.ylli.api.example.model.ExampleModel;
import com.ylli.api.example.service.ExampleService;
import io.seata.core.context.RootContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/seata")
public class SeataController {

    @Autowired
    ExampleService exampleService;

    //@GlobalTransactional
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public Object create(@RequestParam String username, @RequestParam String configName) {
        rpcRestTemplate(configName);
        exampleService.create(new ExampleModel(username, "seata"));
        return "success";
    }

    // TODO fix 行为不一致 是不是 KEY_XID 的原因 KEY_XID 传递失败？
    // 测试是否传递成功
    public Object rpcRestTemplate(String configName) {
        RestTemplate restTemplate = new RestTemplateBuilder().additionalInterceptors((request, body, execution) -> {
                    String xid = RootContext.getXID();
                    if (null != xid) {
                        request.getHeaders().add(RootContext.KEY_XID, xid);
                    }
                    return execution.execute(request, body);
                })
                .build();
        return restTemplate.postForObject("http://192.168.10.8:8080/config", new ConfigModel(configName, "wsy", "2020"), Void.class);
    }
}
