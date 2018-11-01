package com.example.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class TransactionTest {

    @Test
    public void transactionTest() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHost("192.168.99.100");
        connectionFactory.setPort(5672);
        String message = "transactionTest";
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        try {
            channel.txSelect();
            channel.basicPublish("directExchange", "directQueue", null, message.getBytes());
            int t = 1 / 0;
            channel.txCommit();
        } catch (Exception e) {
            e.printStackTrace();
            channel.txRollback();
        } finally {
            channel.close();
            connection.close();
        }
    }

}
