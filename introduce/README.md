## RabbitMQ 特点
    RabbitMQ是一个由Erlang语言开发的AMQP的开源实现。Advanced Message Queue，高级消息队列协议，它是应用层协议的一个开放标准，为面向消息的中间件设计，基于此协议的客户端与消息中间件可传递消息，并不受产品、开发语言等条件的限制。
## RabbitMQ 内部结构
    RabbitMQ是AMQP协议的一个开源实现，所以其内部实际上也是AMQP中的基本概念 

![RabbitMQ 内部结构](https://github.com/Xun-Zhou/RabbitMQ/blob/master/introduce/base.png "RabbitMQ 内部结构")

- Message

        消息由消息头和消息体组成。消息体是不透明的，而消息头则由一系列的可选属性组成，这些属性包括routing-key（路由键）、priority（相对于其他消息的优先权）、delivery-mode（指出该消息可能需要持久性存储）等。
- Publisher

        消息的生产者，也是一个向交换器发布消息的客户端应用程序。
- Exchange

![Exchange](https://github.com/Xun-Zhou/RabbitMQ/blob/master/introduce/exchange.png "Exchange")

    交换机，用来接收生产者发送的消息并将这些消息通过channel路由给服务器中的队列。
- Queue

![Queue](https://github.com/Xun-Zhou/RabbitMQ/blob/master/introduce/queue.png "Queue")

    队列用来保存消息直到发送给消费者。它是消息的容器，也是消息的终点。一个消息可投入一个或多个队列。消息一直在队列里面，等待消费者连接到这个队列将其取走。
- Binding
    
        绑定，用于消息队列和交换器之间的关联。一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则，所以可以将交换器理解成一个由绑定构成的路由表。
- ConnectionFactory、Connection、Channel

        ConnectionFactory、Connection、Channel都是RabbitMQ对外提供的API中最基本的对象。Connection是RabbitMQ的socket链接，它封装了socket协议相关部分逻辑。ConnectionFactory为Connection的制造工厂。 Channel是我们与RabbitMQ打交道的最重要的一个接口，我们大部分的业务操作是在Channel这个接口中完成的，包括定义Queue、定义Exchange、绑定Queue与Exchange、发布消息等。
- Channel

        多路复用连接中的一条独立的双向数据流通道。信道是建立在真实的TCP连接内地虚拟连接，AMQP 命令都是通过信道发出去的，不管是发布消息、订阅队列还是接收消息，这些动作都是通过信道完成。因为对于操作系统来说建立和销毁 TCP 都是非常昂贵的开销，所以引入了信道的概念，以复用一条 TCP 连接。
- Consumer

        消费者
- Virtual Host
    
        虚拟主机，相当于一个命名空间，每个Virtual Host相互隔离，共享相同的身份认证和加密环境的独立服务器域。Virtual Host本质上就是一个mini版的RabbitMQ服务器，拥有自己的队列、交换器、绑定和权限机制。RabbitMQ默认的Virtual Host是"/"。
- Broker

        表示消息队列服务器实体。
## 消息路由
    AMQP中生产者把消息发布到Exchange上，由Binding决定交换器的消息应该发送到那个队列，消息最终到达队列并被消费者接收。

![RabbitMQ 路由过程](https://github.com/Xun-Zhou/RabbitMQ/blob/master/introduce/exchange.png "RabbitMQ 路由过程")

## Exchange 类型
    Exchange有多种分发策略，常用的几种类型：direct、fanout、topic、headers 。
    headers匹配AMQP消息的header而不是路由键，此外headers交换器和direct交换器完全一致，但性能差很多，目前几乎用不到了。
- Direct

![Direct Exchange](https://github.com/Xun-Zhou/RabbitMQ/blob/master/introduce/direct_exchange.png "Direct 路由模式")

    直接匹配 完全匹配路由键routing key，queue与exchange绑定的路由键完全相同，发送到对应的queue，它是完全匹配、单播的模式。
- Fanout

![Fanout Exchange](https://github.com/Xun-Zhou/RabbitMQ/blob/master/introduce/fanout_exchange.png "Fanout 路由模式")

    扇形 与路由键无关，绑定到exchange上的queue都会发送，fanout 类型转发消息是最快的。
- Topic

![Topic Exchange](https://github.com/Xun-Zhou/RabbitMQ/blob/master/introduce/topic_exchange.png "Topic 路由模式")

    主题 topic是通过routingkey来模糊匹配，在topic模式下支持两个特殊字符的匹配
    "*" (星号) 代表任意 一个单词
    "#" (井号) 0个或者多个单词




