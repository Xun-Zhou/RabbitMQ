package com.example.demo.component.reserve;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TopicReserve {

    @RabbitListener(queues = "topicQueue.one")
    public void receive(String message) {
        System.out.println(message);
    }

}
