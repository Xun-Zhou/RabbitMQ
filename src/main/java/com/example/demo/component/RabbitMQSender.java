package com.example.demo.component;

import com.example.demo.config.RabbitConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

@Component("component.RabbitmqSender")
public class RabbitMQSender {

    @Resource(name = "rabbitTemplate")
    private AmqpTemplate amqpTemplate;

    /**
     * direct queue sned
     */
    public void directQueueSender(String message) {
        amqpTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE, RabbitConfig.DIRECT_QUEUE_ROUTING, message, temp -> {
            temp.getMessageProperties().setMessageId(UUID.randomUUID().toString());
            return temp;
        });
    }

    /**
     * topic queue sned
     */
    public void topicQueueSender(String message) {
        amqpTemplate.convertAndSend(RabbitConfig.TOPIC_EXCHANGE, RabbitConfig.TOPIC_KEY, message, temp -> {
            temp.getMessageProperties().setMessageId(UUID.randomUUID().toString());
            return temp;
        });
    }

    /**
     * fanout queue sned
     */
    public void fanoutQueueSender(String message) {
        amqpTemplate.convertAndSend(RabbitConfig.FANOUT_EXCHANGE, message, temp -> {
            temp.getMessageProperties().setMessageId(UUID.randomUUID().toString());
            return temp;
        });
    }

}
