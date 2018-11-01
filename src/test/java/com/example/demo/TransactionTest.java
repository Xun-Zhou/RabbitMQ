package com.example.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
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

    private ConnectionFactory getConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        connectionFactory.setHost("192.168.99.100");
        connectionFactory.setPort(5672);
        return connectionFactory;
    }

    @Test
    public void transactionTest() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = getConnectionFactory();
        String message = "transactionTest";
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        try {
            channel.txSelect();
            channel.basicPublish("directExchange", "transactionQueue", null, message.getBytes("UTF-8"));
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

    @Test
    public void confirmOneTest() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = getConnectionFactory();
        String message = "transactionTest";
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.confirmSelect();
        channel.basicPublish("directExchange", "transactionQueue", null, message.getBytes("UTF-8"));
        if (channel.waitForConfirms()) {
            System.out.println("success");
        }
    }

    @Test
    public void confirmTwoTest() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = getConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.confirmSelect();
        for (int i = 0; i < 10; i++) {
            String message = "transactionTest_" + (i + 1);
            channel.basicPublish("directExchange", "transactionQueue", null, message.getBytes("UTF-8"));
        }
        channel.waitForConfirmsOrDie();
        System.out.println("success");
    }

    @Test
    public void confirmThreeTest() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = getConnectionFactory();
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.confirmSelect();
        for (int i = 0; i < 10; i++) {
            String message = "transactionTest_" + (i + 1);
            channel.basicPublish("directExchange", "transactionQueue", null, message.getBytes("UTF-8"));
        }
        channel.addConfirmListener(new ConfirmListener() {
            @Override
            public void handleAck(long l, boolean b) throws IOException {
                System.out.println(String.format("已确认消息，标识：%d，多个消息：%b", l, b));
            }
            @Override
            public void handleNack(long l, boolean b) throws IOException {
                System.out.println("未确认消息，标识：" + l);
            }
        });
    }

}
