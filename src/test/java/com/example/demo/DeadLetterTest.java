package com.example.demo;

import com.example.demo.component.sender.DeadLetterSender;
import com.example.demo.component.sender.DirectSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class DeadLetterTest {

    @Resource(name = "component.DeadLetterSender")
    private DeadLetterSender deadLetterSender;

    @Test
    public void test() {
        deadLetterSender.send("deadLetterTest");
    }
}
