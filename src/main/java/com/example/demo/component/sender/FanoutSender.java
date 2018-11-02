package com.example.demo.component.sender;

import com.example.demo.config.RabbitConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("component.FanoutSender")
public class FanoutSender {

    @Resource(name = "rabbitTemplate")
    private AmqpTemplate amqpTemplate;

    public void send(String message) {
        amqpTemplate.convertAndSend(RabbitConfig.FANOUT_EXCHANGE, "", message);
    }

}
