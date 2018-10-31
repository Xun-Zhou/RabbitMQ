# RabbitMQ
RabbitMQ安装 安装部署环境 Windows

- Erlang安装

    一般来说安装RabbitMQ之前要安装Erlang，可以去[Erlang官网](http://www.erlang.org/downloads)下载。

- RabbitMQ安装
    
    [RabbitMQ官网](https://www.rabbitmq.com/download.html)下载系统对应安装包，之后解压缩即可。
    
## 维护
- 启动很简单，找到安装后的RabbitMQ所在目录下的sbin目录，直接执行rabbitmq-server即可

    /sbin/rabbitmq-server

- 后台启动

    /sbin/rabbitmq-server -detached
    
- 查询服务器状态

    sbin目录下rabbitmqctl，它提供了RabbitMQ管理需要的几乎一站式解决方案，绝大部分的运维命令它都可以提供。
    
    查询RabbitMQ服务器状态可以用参数status
    
    /sbin/rabbitmqctl status
    
- 关闭RabbitMQ节点

    RabbitMQ是用Erlang语言写的，在Erlang中有两个概念：节点和应用程序。节点就是Erlang虚拟机的每个实例，而多个Erlang应用程序可以运行在同一个节点之上。节点之间可以进行本地通信（不管他们是不是运行在同一台服务器之上）。比如一个运行在节点A上的应用程序可以调用节点B上应用程序的方法，就好像调用本地函数一样。如果应用程序由于某些原因奔溃，Erlang节点会自动尝试重启应用程序。
    
    关闭整个RabbitMQ节点可以用参数stop
    
    /sbin/rabbitmqctl stop
    
    关闭远程节点 -n
    
    /sbin/rabbitmqctl -n rabbit@server.example.com stop  server.example.com为主机名
  
- 关闭、启动RabbitMQ应用程序
    
    - 如果只想关闭应用程序，同时保持Erlang节点运行则可以用stop_app
    
        /sbin/rabbitmqctl stop_app
    
    - 启动应用程序
    
        /sbin/rabbitmqctl start_app
    
- 重置RabbitMQ节点
    
    /sbin/rabbitmqctl reset
    
- 查看

    - 查看已声明的队列
    
        /sbin/rabbitmqctl list_queues
    
    - 查看交换器
    
        /sbin/rabbitmqctl list_exchanges
    
        该命令还可以附加参数，比如列出交换器的名称、类型、是否持久化、是否自动删除
    
        /sbin/rabbitmqctl list_exchanges name type durable auto_delete
    
    - 查看绑定
    
        /sbin/rabbitmqctl list_bindings

# web后台
启动后在浏览器页面输入http://localhost:15672

默认用户名密码都是guest

![Web](https://github.com/Xun-Zhou/RabbitMQ/blob/master/install/web.png "Web")