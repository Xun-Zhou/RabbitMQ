package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String DIRECT_EXCHANGE = "directExchange";

    public static final String TOPIC_EXCHANGE = "topicExchange";

    public static final String FANOUT_EXCHANGE = "fanoutExchange";

    public static final String DIRECT_QUEUE_ROUTING = "directQueue";

    public static final String TOPIC_QUEUE_ROUTING_ONE = "topicQueue.one";

    public static final String FANOUT_QUEUE_ROUTING_ONE = "fanoutQueue.one";

    public static final String TOPIC_KEY = "topicQueue.#";

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private Integer port;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Value("${spring.rabbitmq.username}")
    private String userName;

    @Value("${spring.rabbitmq.password}")
    private String passWord;

    /**
     * 链接工厂
     */
    @Bean(name = "connectionFactory")
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(this.host, this.port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(passWord);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    /**
     * rabbitTemplate
     */
    @Bean(name = "rabbitTemplate")
    public RabbitTemplate primaryRabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    /**
     * ----------------交换机----------------
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    /**
     * ----------------队列----------------
     */
    @Bean
    public Queue directQueue() {
        return new Queue(DIRECT_QUEUE_ROUTING, true);
    }

    @Bean
    public Queue topicQueueOne() {
        return new Queue(TOPIC_QUEUE_ROUTING_ONE, true);
    }

    @Bean
    public Queue fanoutQueueOne() {
        return new Queue(FANOUT_QUEUE_ROUTING_ONE, true);
    }

    /**
     * ----------------绑定----------------
     */
    @Bean
    public Binding bindDirectExchangeDirect(Queue directQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(directQueue).to(directExchange).with(DIRECT_QUEUE_ROUTING);
    }

    @Bean
    public Binding bindTopicExchangeTopicOne(Queue topicQueueOne, TopicExchange topicExchange) {
        return BindingBuilder.bind(topicQueueOne).to(topicExchange).with(TOPIC_KEY);
    }

    @Bean
    public Binding bindFanoutExchangeFanoutOne(Queue fanoutQueueOne, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(fanoutQueueOne).to(fanoutExchange);
    }

}
