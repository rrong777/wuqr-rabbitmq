package com.wuqr.rabbitmq.one;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author wql78
 * @title: Producer
 * @description: one包，第一版代码，two、three…… 接下来为第二版第三版
 * @date 2021-09-20 16:12:48
 */
public class Producer {
    // 队列名称
    public static final String QUEUE_NAME = "hello";

    /**
     * 发送消息
     * @param args
     */
    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 工厂IP 连接RabbitMQ的队列
        factory.setHost("192.168.0.110");
        // 用户名
        factory.setUsername("admin");
        // 密码
        factory.setPassword("123"); // 到这一步
        // 创建连接
        Connection connection = factory.newConnection();
        // 获取信道
        Channel channel = connection.createChannel();
        /**
         * 声明一个队列
         * 参数
         * String queue 队列名称
         * boolean durable 队列里面的消息是否需要持久化，默认情况消息存储在内存中
         * boolean exclusive 队列是否需要排他,即该队列是否只供一个消费者进行性消费，是否进行消息共享 true：可以多个消费者消费队列
         * boolean autoDelete 是否需要自动删除 最后一个消费者断开连接之后 该队列是否自动删除，true 自动删除
         * Map<String, Object> argument 其他队列参数 其他参数
         *
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        // 发消息
        String message = "hello world"; // 初次使用
        /**
         * 基础发布，生产一个消息到队列里面
         * 第一个参数交换机，生产到哪个交换机，先传空串
         * 第二个参数路由的key值，本次、传队列名称，
         * 第三个参数本次也没有 传null，
         * 第四个参数是消息体的二进制信息
         */
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
        System.out.println("消息发送完毕！");
    }
}
