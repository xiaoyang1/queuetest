#queuetest
这个工程主要的学习目的：学习rabbitMQ队列的基本机制和exchange和 subscribe和路由规则，测试了RPC功能。
重在通过代码理解rabbitMQ队列 的 消息处理机制，即：对于没有持久化消息，如果没有订阅者，则丢失，对于持久化消息，被consume后同样会被垃圾回收机制回收。
但是如果没有被consume，就会持久化写入磁盘。这个可以在磁盘找到的。



