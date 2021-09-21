package com.wuqr.rabbitmq.one;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author wql78
 * @title: Consumer
 * @description: 消费者，从队列接收消息并且消费消息
 * @date 2021-09-21 16:08:40
 */
public class Consumer {
    // 队列名称
    public static final String QUEUE_NAME = "hello";
    // 接收消息
    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.0.110");
        factory.setUsername("admin");
        factory.setPassword("123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 声明未成功消费的回调 拉姆达表达式，函数声明  直接入参声明两个形参，类型可以不写，反正前面类型已经定好了，这个方法也是固定的。
        // 详细可以看DeliverCallback 里面的handle方法，后面的lambda表达式就是那个方法的匿名实现，跟匿名内部类是一个意思
        // 声明接收消息
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            byte[] body = message.getBody();
            System.out.println(new String(body));
        };
        // 声明取消消息
        CancelCallback cancelCallback = consumerTag -> {
            System.out.println("消息消费被中断！");
        };
        /**
         * 消费者消费消息
         * 方法4个参数如下
         * 1. 消费队列名称
         * 2. 消费成功之后是否要自动应答 true代表自动应答，false 手动应答
         * 3.  第三个参数是接收消息回调的函数式接口
         * 4. 消费者取消消费消息的回调, 消息如果被正常接收，这个回调没有任何意义。
         */
        // 这行代码去接收消息，接收到消息之后就会把消息传递给deliverCallback
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
