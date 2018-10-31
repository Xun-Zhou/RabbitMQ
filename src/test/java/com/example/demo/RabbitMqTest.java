package com.example.demo;

import com.example.demo.component.RabbitMQSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class RabbitMqTest {

    @Resource(name = "component.RabbitmqSender")
    private RabbitMQSender rabbitMQSender;

    @Test
    public void directQueueTest(){
        rabbitMQSender.directQueueSender("directQueueTest");
    }

    @Test
    public void topicQueueTest(){
        rabbitMQSender.topicQueueSender("topicQueueTest");
    }

    @Test
    public void fanoutQueueTest(){
        rabbitMQSender.fanoutQueueSender("fanoutQueueTest");
    }

}
