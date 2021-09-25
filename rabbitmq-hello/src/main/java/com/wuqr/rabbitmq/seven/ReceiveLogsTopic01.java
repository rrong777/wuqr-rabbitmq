package com.wuqr.rabbitmq.seven;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author wql78
 * @title: ReceiveLogsTopic01
 * @description: 声明主题交换机 及相关队列
 * 消费者C1
 * @date 2021-09-25 23:12:37
 */
public class ReceiveLogsTopic01 {
    // 交换机名称
    public static final String EXCHANGE_NAME = "topic_logs";

    // 接收消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 声明队列
        String queueName = "Q1";
        channel.queueDeclare(queueName, false, false, false, null);
        // 信道进行队列的捆绑
        channel.queueBind(queueName, EXCHANGE_NAME, "*.orange.*");
        System.out.println("C1等待接收消息……");
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(new String(message.getBody(), StandardCharsets.UTF_8));
            System.out.println("接收队列： " + queueName + ",绑定键：" + message.getEnvelope().getRoutingKey());
        };
        // 接收消息
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {} );
    }
}
