# RabbitMQ集群
    RabbitMQ 会始终记录以下四种类型的内部元数据：
   - 队列元数据

    包括队列名称和它们的属性，比如是否可持久化，是否自动删除
   - 交换器元数据

    交换器名称、类型、属性
   - 绑定元数据

    内部是一张表格记录如何将消息路由到队列
   - vhost 元数据

    为 vhost 内部的队列、交换器、绑定提供命名空间和安全属性

# RabbitMQ集群概述
  
    在单一节点中，RabbitMQ 会将所有这些信息存储在内存中，同时将标记为可持久化的队列、交换器、绑定存储到硬盘上。存到硬盘上可以确保队列和交换器在节点重启后能够重建。而在集群模式下同样也提供两种选择：存到硬盘上（独立节点的默认设置）、存在内存中。

    如果在集群中创建队列，集群只会在单个节点而不是所有节点上创建完整的队列信息（元数据、状态、内容）。结果是只有队列的持有者节点知道有关队列的所有信息，因此当集群节点崩溃时，该节点的队列和绑定就消失了，并且任何匹配该队列的绑定的新消息也丢失了。RabbitMQ 2.6.0之后提供了镜像队列以避免集群节点故障导致的队列内容不可用。

    RabbitMQ 集群中可以共享 user、vhost、exchange等，所有的数据和状态都是在所有节点上复制的，例外就是上面所说的消息队列。RabbitMQ 节点可以动态的加入到集群中。

    RabbitMQ 只要求集群中至少有一个磁盘节点，所有其他节点可以是内存节点，当节点加入或者离开集群时，它们必须要将该变更通知到至少一个磁盘节点。如果只有一个磁盘节点，刚好又是该节点崩溃了，那么集群可以继续路由消息，但不能创建队列、创建交换器、创建绑定、添加用户、更改权限、添加或删除集群节点。换句话说集群中的唯一磁盘节点崩溃的话，集群仍然可以运行，该节点恢复之前无法更改任何东西。
    
    内存节点唯一存储到磁盘上的是磁盘节点的地址。

# RabbitMQ集群配置和启动

    资源有限，故使用docker搭建单机RabbitMQ集群。
    
    docker下载RabbitMQ镜像
    
    docker pull rabbitmq:3.6.15-management
    
    -management是包含网页控制台的版本
    
    docker images命令查看安装完成的镜像
    
   - 安装RabbitMQ 启动3个RabbitMQ节点

    docker run -d --hostname rabbit1 --name myrabbit1 -p 15672:15672 -p 5672:5672 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.7.8-management
    
    docker run -d --hostname rabbit2 --name myrabbit2 -p 5673:5672 --link myrabbit1:rabbit1 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.7.8-management
    
    docker run -d --hostname rabbit3 --name myrabbit3 -p 5674:5672 --link myrabbit1:rabbit1 --link myrabbit2:rabbit2 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.7.8-management
    
    多个容器之间使用“--link”连接，Erlang Cookie值必须相同，也就是RABBITMQ_ERLANG_COOKIE参数的值必须相同。
    
   - 加入RabbitMQ节点到集群
   
     - 设置节点1
     
            docker exec -it myrabbit1 /bin/bash   //进入节点1容器
            rabbitmqctl stop_app     //停止RabbitMQ应用
            rabbitmqctl reset    //重置
            rabbitmqctl start_app    //启动RabbitMQ应用
            exit     //退出容器
           
     - 设置节点2
          
            docker exec -it myrabbit2 /bin/bash
            rabbitmqctl stop_app
            rabbitmqctl reset
            rabbitmqctl join_cluster --ram rabbit@rabbit1    //加入集群 --ram指定为内存节点 未指定默认为磁盘节点
            rabbitmqctl start_app
            exit
     
     - 设置节点3
               
            docker exec -it myrabbit3 /bin/bash
            rabbitmqctl stop_app
            rabbitmqctl reset
            rabbitmqctl join_cluster --ram rabbit@rabbit1
            rabbitmqctl start_app
            rabbitmqctl cluster_status  //查看集群状态
            exit
   
   docker-machine ls可以查看docker主机ip
   
   浏览器页面输入ip:15672  用户名密码均为guest进入管理页面
   
   ![Manage](https://github.com/Xun-Zhou/RabbitMQ/blob/master/cluster/manage.png "Manage")