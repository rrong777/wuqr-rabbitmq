package com.wuqr.rabbitmq.one;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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
        Map<String, Object> arguments = new HashMap<>();
        // 默认方法是允许 0-255，这里参数设置了最大的就是10 允许优先级0-10，这个参数也可以在rabbitmq管理页面新增队列的时候设置
        // 设置了这个参数，并且发消息的时候设置了消息的优先级就会按照优先级高的消息先出队，这里没设置的话，消息设置了优先级也没用
        arguments.put("x-max-priority", 10);
        channel.queueDeclare(QUEUE_NAME, false, false, false, arguments);
        for(int i = 1; i < 11; i++) {
            String message = "msg" + i;
            if(i == 5) {
                AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(5).build();

                channel.basicPublish("", QUEUE_NAME, properties, message.getBytes(StandardCharsets.UTF_8));
            } else {
                // 其他的直接发送 不设置优先级
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));

            }
        }
        System.out.println("发送消息完成");
    }
}
