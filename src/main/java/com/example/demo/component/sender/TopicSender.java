package com.example.demo.component.sender;

import com.example.demo.config.RabbitConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("component.TopicSender")
public class TopicSender {

    @Resource(name = "rabbitTemplate")
    private AmqpTemplate amqpTemplate;

    public void send(String message) {
        amqpTemplate.convertAndSend(RabbitConfig.TOPIC_EXCHANGE, RabbitConfig.TOPIC_KEY, message);
    }

}
