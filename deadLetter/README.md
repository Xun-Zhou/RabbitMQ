# RabbitMQ延时队列
    rabbitmq本身不具有延时消息队列的功能，但是可以通过TTL(Time To Live)、DLX(Dead Letter Exchanges)、DLK(x-dead-letter-routing-key)特性实现。
    其原理给消息设置过期时间，在消息队列上为过期消息指定转发器，这样消息过期后会转发到与指定转发器匹配的队列上，变向实现延时队列。
    
    定义queue时，加入延时参数，其他操作与普通消息发送接收一致，接收方要监听的队列是过期后转发的队列。
    
```java
@Bean
public Queue dieQueue() {
    Map<String, Object> arguments = new HashMap<>();
    arguments.put("x-message-ttl",20000);//过期时间 毫秒
    arguments.put("x-dead-letter-exchange",DEAD_LETTER_EXCHANGE);//过期后转发交换机
    arguments.put("x-dead-letter-routing-key",LETTER_QUEUE_ROUTING);//过期后转发队列
    return new Queue(DEAD_QUEUE_ROUTING, true, false,false, arguments);
}
```
    
   ![DeadLetter](https://github.com/Xun-Zhou/RabbitMQ/blob/master/deadLetter/deadLetter.png "DeadLetter")