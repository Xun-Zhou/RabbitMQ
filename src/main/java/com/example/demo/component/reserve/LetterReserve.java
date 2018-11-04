package com.example.demo.component.reserve;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class LetterReserve {

    @RabbitListener(queues = "letterQueue")
    public void receive(String message) {
        System.out.println(message);
    }

}
