package com.example.demo.config;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    public static final String DIRECT_EXCHANGE = "directExchange";

    public static final String TOPIC_EXCHANGE = "topicExchange";

    public static final String FANOUT_EXCHANGE = "fanoutExchange";

    public static final String DEAD_LETTER_EXCHANGE = "deadLetterExchange";

    public static final String DIRECT_QUEUE_ROUTING = "directQueue";

    public static final String TOPIC_QUEUE_ROUTING_ONE = "topicQueue.one";

    public static final String FANOUT_QUEUE_ROUTING_ONE = "fanoutQueue.one";

    public static final String TOPIC_KEY = "topicQueue.#";

    public static final String TRANSACTION_QUEUE_ROUTING = "transactionQueue";

    public static final String CALL_BACK_QUEUE_ROUTING = "callBackQueue";

    public static final String DEAD_QUEUE_ROUTING = "deadQueue";

    public static final String LETTER_QUEUE_ROUTING = "letterQueue";

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

    @Value("${spring.rabbitmq.publisher-confirms}")
    private Boolean publisherConfirms;

    @Value("${spring.rabbitmq.publisher-returns}")
    private Boolean publisherReturns;

    /**
     * 链接工厂
     */
    @Bean(name = "connectionFactory")
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(this.host, this.port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(passWord);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(publisherConfirms);
        connectionFactory.setPublisherReturns(publisherReturns);
        return connectionFactory;
    }

    /**
     * rabbitTemplate
     */
    @Bean(name = "rabbitTemplate")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate primaryRabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
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

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
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

    @Bean
    public Queue transactionQueue() {
        return new Queue(TRANSACTION_QUEUE_ROUTING, true);
    }

    @Bean
    public Queue callBackQueue() {
        return new Queue(CALL_BACK_QUEUE_ROUTING, true);
    }

    @Bean
    public Queue dieQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-message-ttl",20000);//过期时间 毫秒
        arguments.put("x-dead-letter-exchange",DEAD_LETTER_EXCHANGE);//过期后发送交换机
        arguments.put("x-dead-letter-routing-key",LETTER_QUEUE_ROUTING);//过期后发送队列
        return new Queue(DEAD_QUEUE_ROUTING, true, false,false, arguments);
    }

    @Bean
    public Queue letterQueue() {
        return new Queue(LETTER_QUEUE_ROUTING, true);
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

    @Bean
    public Binding bindDirectExchangeTransaction(Queue transactionQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(transactionQueue).to(directExchange).with(TRANSACTION_QUEUE_ROUTING);
    }

    @Bean
    public Binding bindDirectExchangeCallBack(Queue callBackQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(callBackQueue).to(directExchange).with(CALL_BACK_QUEUE_ROUTING);
    }

    @Bean
    public Binding bindLetterExchangeDie(Queue dieQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(dieQueue).to(deadLetterExchange).with(DEAD_QUEUE_ROUTING);
    }

    @Bean
    public Binding bindLetterExchangeLetter(Queue letterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(letterQueue).to(deadLetterExchange).with(LETTER_QUEUE_ROUTING);
    }

    /**
     * ----------------------listener container--------------------------
     */
    @Bean
    public SimpleMessageListenerContainer messageContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueues(callBackQueue());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        ChannelAwareMessageListener channelAwareMessageListener = (message, channel) -> {
            byte[] body = message.getBody();
            System.out.println("receive msg : " + new String(body));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        };
        container.setMessageListener(channelAwareMessageListener);
        return container;
    }
}
