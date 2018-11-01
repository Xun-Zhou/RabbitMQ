package com.example.demo.component;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQReserve {

    /**direct queue接收*/
    @RabbitListener(queues = "directQueue")
    public void directQueueReceive(String message) {
        System.out.println(message);
    }

    /**topic queue接收*/
    @RabbitListener(queues = "topicQueue.one")
    public void topicQueueReceive(String message) {
        System.out.println(message);
    }

    /**fanout queue接收*/
    @RabbitListener(queues = "fanoutQueue.one")
    public void fanoutQueueReceive(String message) {
        System.out.println(message);
    }
}
