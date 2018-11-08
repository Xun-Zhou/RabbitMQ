# RabbitMQ事务
    RabbitMQ有两种方式来解决事务问题:通过AMQP提供的事务机制实现、使用发送者确认模式实现。两者都是针对channel，
    一个具有事务的channel不能放入到确认模式，同样确认模式下的channel不能用事务。
- 通过AMQP提供的事务机制实现

        事务的实现主要是对信道（Channel）的设置，主要的方法有三个
        
        1.channel.txSelect()声明启动事务模式；
          
        2.channel.txCommit()提交事务；
          
        3.channel.txRollback()回滚事务；
```java
  //链接工厂
  ConnectionFactory connectionFactory = new ConnectionFactory();
  connectionFactory.setUsername("guest");
  connectionFactory.setPassword("guest");
  connectionFactory.setVirtualHost("/");
  connectionFactory.setHost("127.0.0.1");
  connectionFactory.setPort(5672);
  String message = "transactionTest";
  //获取链接
  Connection connection = connectionFactory.newConnection();
  //创建通道
  Channel channel = connection.createChannel();
  try {
      //开启事务
      channel.txSelect();
      //发布消息
      channel.basicPublish("exchange", "queue", null, message.getBytes());
      //异常模拟
      int t = 1 / 0;
      //提交事务
      channel.txCommit();
  } catch (Exception e) {
      e.printStackTrace();
      //回滚事务
      channel.txRollback();
  } finally {
      channel.close();
      connection.close();
  }
```
- 使用发送者确认模式实现
    
        由于使用事务性能较差，通常使用Confirm发送方确认模式
        
     - 普通Confirm模式
     
            推送消息之前，channel.confirmSelect()声明开启发送方确认模式，再使用channel.waitForConfirms()等待消息被服务器确认即可
```java
  //链接工厂
  ConnectionFactory connectionFactory = new ConnectionFactory();
  connectionFactory.setUsername("guest");
  connectionFactory.setPassword("guest");
  connectionFactory.setVirtualHost("/");
  connectionFactory.setHost("127.0.0.1");
  connectionFactory.setPort(5672);
  String message = "transactionTest";
  //获取链接
  Connection connection = connectionFactory.newConnection();
  //创建通道
  Channel channel = connection.createChannel();
  //开启发送确认
  channel.confirmSelect();
  //发布消息
  channel.basicPublish("directExchange", "directQueue", null, message.getBytes());
  //确认
  if(channel.waitForConfirms()){
      System.out.println("success");
  }
```
     - 批量Confirm模式
     
            channel.waitForConfirmsOrDie()，等所有的消息发送之后才会执行后面代码，只要有一个消息未被确认就会抛出IOException异常
```java
  //链接工厂
  ConnectionFactory connectionFactory = new ConnectionFactory();
  connectionFactory.setUsername("guest");
  connectionFactory.setPassword("guest");
  connectionFactory.setVirtualHost("/");
  connectionFactory.setHost("127.0.0.1");
  connectionFactory.setPort(5672);
  String message = "transactionTest";
  //获取链接
  Connection connection = connectionFactory.newConnection();
  //创建通道
  Channel channel = connection.createChannel();
  //开启发送确认
  channel.confirmSelect();
  for (int i = 0; i < 10; i++) {
      String message = "transactionTest_" + (i + 1);
      channel.basicPublish("directExchange", "directQueue", null, message.getBytes("UTF-8"));
  }
  channel.waitForConfirmsOrDie();
  System.out.println("success");
```
     - 异步Confirm模式
            
```java
  //链接工厂
  ConnectionFactory connectionFactory = new ConnectionFactory();
  connectionFactory.setUsername("guest");
  connectionFactory.setPassword("guest");
  connectionFactory.setVirtualHost("/");
  connectionFactory.setHost("127.0.0.1");
  connectionFactory.setPort(5672);
  String message = "transactionTest";
  //获取链接
  Connection connection = connectionFactory.newConnection();
  //创建通道
  Channel channel = connection.createChannel();
  //开启发送确认
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
```         
- 结合spring boot来使用确认机制
    
    - 发布者发布消息，服务器确认回调
```java
@Component
public class CallBackSender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Resource(name = "rabbitTemplate")
    private RabbitTemplate amqpTemplate;

    public void send(String message) {
        amqpTemplate.setConfirmCallback(this);
        amqpTemplate.setReturnCallback(this);
        amqpTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE, RabbitConfig.DIRECT_QUEUE_ROUTING, message);
        //amqpTemplate.convertAndSend(RabbitConfig.DIRECT_EXCHANGE, "XXXXXXXXXX", message);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        System.out.println("call back confirm: " + correlationData + " ACK : " + b + " cause : "+ s);
    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        System.out.println("returned message: " + message);
    }
}
```
    实现RabbitTemplate.ConfirmCallback，重写confirm方法，发布者发布消息后会得到服务器的返回。
    单例模式下的RabbitTemplate只能设置一次ConfirmCallback。
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) 改为多例模式。
    
    实现RabbitTemplate.ConfirmCallback，重写returnedMessage方法，发布者发布消息后，exchange到queue失败，会得到服务器的返回信息。
    需要设置rabbitTemplate.setMandatory(true);否则不会回调，消息丢失。
    如果消息没有到exchange,则confirm回调,ack=false
    如果消息到达exchange,则confirm回调,ack=true
    exchange到queue成功,则不回调return
    exchange到queue失败,则回调return(需设置mandatory=true,否则不会回调，消息丢失)
    
    - 消费方接收消息，告知rabbitmq消息消费成功或失败。
    
    自动确认会在消息发送给消费者后立即确认，如果手动则当消费者调用ack,nack,reject几种方法时进行确认。
    手动确认需要设置
    spring.rabbitmq.listener.simple.acknowledge-mode=manual默认为auto自动确认
    所有queue需要消费者手动确认后，rabbitmq队列才将消息删除
```java
@Component
public class CallBackReserve {

    @RabbitListener(queues = "callBackQueue")
    public void reserve(Message message, Channel channel) throws Exception {
        try{
            System.out.println("consumer"+":"+new String(message.getBody()));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch(Exception e){
            e.printStackTrace();
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
        }
    }
}
```
    可以使用SimpleMessageListenerContainer指定部分队列需要手动确认
```java
@Bean
public SimpleMessageListenerContainer messageContainer() {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
    container.setQueues(callBackQueue());//queue1(),queue()2,...绑定队列
    container.setExposeListenerChannel(true);
    container.setMaxConcurrentConsumers(1);
    container.setConcurrentConsumers(1);
    container.setAcknowledgeMode(AcknowledgeMode.MANUAL);//设置通知方式为手动确认
    ChannelAwareMessageListener channelAwareMessageListener = (message, channel) -> {
        byte[] body = message.getBody();
        System.out.println("receive msg : " + new String(body));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    };
    container.setMessageListener(channelAwareMessageListener);
    return container;
}
```