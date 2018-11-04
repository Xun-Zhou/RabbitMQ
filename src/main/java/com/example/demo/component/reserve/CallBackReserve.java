package com.example.demo.component.reserve;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CallBackReserve {

    //@RabbitListener(queues = "callBackQueue")
    public void reserve(Message message, Channel channel) throws Exception {
        try {
            System.out.println("consumer" + ":" + new String(message.getBody()));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
