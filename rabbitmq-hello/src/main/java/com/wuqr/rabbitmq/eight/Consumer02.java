package com.wuqr.rabbitmq.eight;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wql78
 * @title: Consumer01
 * @description: 死信队列实战
 * 消费者02
 * @date 2021-09-27 22:28:36
 */
public class Consumer02 {

    // 死信队列名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();



        System.out.println("等待接收消息…………");

        DeliverCallback deliverCallback = (consumerTag, message ) -> {
            System.out.println("Consumer01接收的消息是：" + new String(message.getBody(), StandardCharsets.UTF_8));
        };
        // 消费死信队列 在consumer02这里，没有什么死信队列的概念，对他来说，这就是一个普通队列
        channel.basicConsume(DEAD_QUEUE, true, deliverCallback, consumerTag -> {});
    }

}
