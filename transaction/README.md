# RabbitMQ事务
    RabbitMQ有两种方式来解决事务问题
- 通过AMQP提供的事务机制实现

        事务的实现主要是对信道（Channel）的设置，主要的方法有三个
        
        1.channel.txSelect()声明启动事务模式；
          
        2.channel.txCommit()提交事务；
          
        3.channel.txRollback()回滚事务；
    ```
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
    ```
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
    ```
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
            
    ```
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