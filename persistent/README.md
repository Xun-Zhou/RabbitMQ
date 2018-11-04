# RabbitMQ持久化
    消息在传输过程中,可能会出现各种异常失败甚至宕机情况,为了保证消息传输的可靠性,需要进行持久化,也就是在数据写在磁盘上。
    消息队列持久化包括三部分:1.Message持久化,也就是发送时消息持久化。2.队列持久化。3.交换机持久化。
    设置消息持久化，消息和队列必须持久化，如果自定义exchange，exchange也需要持久化消息发布错误。
- 消息持久化
    - 使用channel
        
            AMQP消息持久化针对channel，在发布消息时设置BasicProperties为MessageProperties.PERSISTENT_TEXT_PLAIN实现消息持久化
            channel.basicPublish("exchange", "queue", MessageProperties.PERSISTENT_TEXT_PLAIN, "message".getBytes());
            
    - 发布消息时调用sendAndReceive();
            
            Message message = MessageBuilder.withBody("message".getBytes()).build();//消息构建
            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);//设置DeliveryMode为PERSISTENT
            rabbitTemplate.sendAndReceive("exchange", "queue",message);
            
            rabbitTemplate.convertAndSend();方法默认是消息持久化。
            MessageProperties.class部分源码
            ```
            public MessageProperties() {
                this.deliveryMode = DEFAULT_DELIVERY_MODE;
                this.priority = DEFAULT_PRIORITY;
            }
            static {
                DEFAULT_DELIVERY_MODE = MessageDeliveryMode.PERSISTENT;
                DEFAULT_PRIORITY = 0;
            }
            ```
- 队列持久化
    - 使用channel
    
            channel创建队列时声明持久化，第二个参数表明为持久化队列
            channel.queueDeclare("queue.persistent.name", true, false, false, null);
    - java配置
    
            new queue("名称","是否持久化");
            ```
            @Bean
            public Queue queue() {
                return new Queue("queue", true);
            }
            ```
- 交换机持久化
    - 使用channel
    
            channel.exchangeDeclare(exchangeName, "direct/topic/header/fanout", true);声明时durable字段设置为true即可。
        
    - java配置
    
            DirectExchange(String name, boolean durable, boolean autoDelete)
            默认为持久化
            AbstractExchange.class源码
            ```
            public AbstractExchange(String name) {
                this(name, true, false);
            }
            ```
