package com.ylli.api.mq;

import com.google.gson.Gson;
import com.ylli.api.config.rocketmq.RocketMQConfiguration;
import com.ylli.api.example.service.ConfigService;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rocketmq")
public class RocketMQController {

    @Autowired(required = false)
    RocketMQConfiguration mq;

    @Autowired
    ConfigService configService;

    @GetMapping("/send")
    void transactionSend(String msg) throws MQClientException {
        TransactionSendResult result1 = mq.sendTransactionMessage(msg, () -> {
            return configService.create(msg, "msgValue", null) == 1;
        });
        System.out.println(new Gson().toJson(result1));
    }
}
