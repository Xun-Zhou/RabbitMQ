package com.example.demo.component.reserve;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionReserve {

    @RabbitListener(queues = "transactionQueue")
    public void receive(byte message[]) {
        String temp = new String(message);
        System.out.println(temp);
    }
}
