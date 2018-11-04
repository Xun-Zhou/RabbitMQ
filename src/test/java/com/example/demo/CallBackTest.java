package com.example.demo;

import com.example.demo.component.sender.CallBackSender;
import com.example.demo.component.sender.TopicSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class CallBackTest {

    @Resource(name = "component.CallBackSender")
    private CallBackSender callBackSender;

    @Test
    public void test() {
        callBackSender.send("call back test");
    }
}
