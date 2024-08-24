package com.ylli.api.example;

import com.ylli.api.example.model.ExampleModel;
import com.ylli.api.example.service.ExampleService;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.shardingsphere.transaction.annotation.ShardingSphereTransactionType;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;


@RestController
@RequestMapping("/seata")
public class SeataController {

    @Autowired
    ExampleService exampleService;

    /**
     * shardingsphere 集成 seats
     * 当引入 'org.apache.shardingsphere:shardingsphere-transaction-base-seata-at:5.2.1' 后，在函数上使用 io.seata.spring.annotation.GlobalTransactional 注解，这是不被允许的。（https://shardingsphere.apache.org/document/current/cn/user-manual/shardingsphere-jdbc/special-api/transaction/seata/）
     *
     */
    //@GlobalTransactional
    @ShardingSphereTransactionType(TransactionType.BASE)
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public Object create(@RequestParam String username, @RequestParam String bizTag) {
        rpcRestTemplate(bizTag);
        exampleService.create(new ExampleModel(username, "seata"));
        return "success";
    }

    public Object rpcRestTemplate(String bizTag) {
        RestTemplate restTemplate = new RestTemplateBuilder().additionalInterceptors((request, body, execution) -> {
                    String xid = RootContext.getXID();
                    if (null != xid) {
                        request.getHeaders().add(RootContext.KEY_XID, xid);
                    }
                    return execution.execute(request, body);
                })
                .build();
        return restTemplate.postForObject("http://localhost:18080/leaf", new LeafVo(bizTag), String.class);
    }

    public Object rpc(String bizTag) {
        return WebClient.create().post()
                .uri("http://localhost:18080/leaf")
                .header(RootContext.KEY_XID, RootContext.getXID())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new LeafVo(bizTag))
                .retrieve()
                .bodyToMono(String.class);
    }

    static class LeafVo {
        public String bizTag;
        public Long idx = 1L;
        public Integer step = 2000;
        public String description = "default description";

        public LeafVo(String bizTag) {
            this.bizTag = bizTag;
        }
    }

    @GetMapping("/test")
    public Object test(@RequestParam String bizTag) {
        return rpc(bizTag);
    }
}
