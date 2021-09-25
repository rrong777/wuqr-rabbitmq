package com.wuqr.rabbitmq.six;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;

/**
 * @author wql78
 * @title: ReceiveLogsDirect01
 * @description: 测试直接交换机 消费者
 * @date 2021-09-25 21:44:12
 */
public class ReceiveLogsDirect01 {
    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 声明一个交换机
        // BuiltinExchangeType.DIRECT 就是 ”direct“； 枚举，提前内置好的类型
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        // 声明一个队列
        channel.queueDeclare("console", false, false, false, null);

        // 多重绑定，声明一个队列绑定多个routingKey
        channel.queueBind("console", EXCHANGE_NAME, "info");
        channel.queueBind("console", EXCHANGE_NAME, "warnting");
        System.out.println("ReceiveLogsDirect01等待接收消息，把接收到的消息打印再屏幕……");

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("ReceiveLogsDirect01控制台打印接收到的消息：" + new String(message.getBody(), "UTF-8"));
        };

        channel.basicConsume("console", true, deliverCallback, consumerTag -> {});
    }
}
