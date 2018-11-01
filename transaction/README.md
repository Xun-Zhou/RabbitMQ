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