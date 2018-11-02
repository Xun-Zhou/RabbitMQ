package com.example.demo;

import com.example.demo.component.sender.TopicSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class TopicTest {

    @Resource(name = "component.TopicSender")
    private TopicSender topicSender;

    @Test
    public void test() {
        topicSender.send("topicQueueTest");
    }
}
