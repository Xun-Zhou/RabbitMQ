package com.example.demo;

import com.example.demo.component.sender.FanoutSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class FanoutTest {

    @Resource(name = "component.FanoutSender")
    private FanoutSender fanoutSender;

    @Test
    public void test() {
        fanoutSender.send("fanoutQueueTest");
    }
}
