# RabbitMQ
RabbitMQ原理及使用
## RabbitMQ 特点
RabbitMQ是一个由Erlang语言开发的AMQP的开源实现。Advanced Message Queue，高级消息队列协议，它是应用层协议的一个开放标准，为面向消息的中间件设计，基于此协议的客户端与消息中间件可传递消息，并不受产品、开发语言等条件的限制。
## RabbitMQ 内部结构
RabbitMQ是AMQP协议的一个开源实现，所以其内部实际上也是AMQP中的基本概念 
![RabbitMQ 内部结构](https://github.com/Xun-Zhou/RabbitMQ/blob/master/base.png "RabbitMQ 内部结构")

- Message
消息由消息头和消息体组成。消息体是不透明的，而消息头则由一系列的可选属性组成，这些属性包括routing-key（路由键）、priority（相对于其他消息的优先权）、delivery-mode（指出该消息可能需要持久性存储）等。
- Publisher
消息的生产者，也是一个向交换器发布消息的客户端应用程序。
- Exchange
交换机，用来接收生产者发送的消息并将这些消息通过channel路由给服务器中的队列。
- Queue
队列用来保存消息直到发送给消费者。它是消息的容器，也是消息的终点。一个消息可投入一个或多个队列。消息一直在队列里面，等待消费者连接到这个队列将其取走。
- Binding
绑定，用于消息队列和交换器之间的关联。一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则，所以可以将交换器理解成一个由绑定构成的路由表。
- Connection
网络连接
- Channel
多路复用连接中的一条独立的双向数据流通道。信道是建立在真实的TCP连接内地虚拟连接，AMQP 命令都是通过信道发出去的，不管是发布消息、订阅队列还是接收消息，这些动作都是通过信道完成。因为对于操作系统来说建立和销毁 TCP 都是非常昂贵的开销，所以引入了信道的概念，以复用一条 TCP 连接。
- Consumer
消费者
- Virtual Host
虚拟主机，相当于一个命名空间，每个Virtual Host相互隔离，共享相同的身份认证和加密环境的独立服务器域。Virtual Host本质上就是一个mini版的RabbitMQ服务器，拥有自己的队列、交换器、绑定和权限机制。RabbitMQ默认的Virtual Host是"/"。
- Broker
表示消息队列服务器实体。

