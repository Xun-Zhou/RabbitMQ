package com.example.demo.component.sender;

import com.example.demo.config.RabbitConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("component.CallBackSender")
public class CallBackSender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Resource(name = "rabbitTemplate")
    private RabbitTemplate amqpTemplate;

    public void send(String message) {
        amqpTemplate.setConfirmCallback(this);
        amqpTemplate.setReturnCallback(this);
        amqpTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE, RabbitConfig.CALL_BACK_QUEUE_ROUTING, message);
        //amqpTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE, "XXXXXXXXXX", message);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        System.out.println("call back confirm: " + correlationData + " ACK : " + b + " cause : "+ s);
    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        System.out.println("returned message: " + message);
    }
}
