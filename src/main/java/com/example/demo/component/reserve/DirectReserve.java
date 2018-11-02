package com.example.demo.component.reserve;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DirectReserve {

    @RabbitListener(queues = "directQueue")
    public void receive(String message) {
        System.out.println(message);
    }

}
