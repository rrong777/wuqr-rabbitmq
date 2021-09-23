package com.wuqr.rabbitmq.two;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;

/**
 * @author wql78
 * @title: Worker01
 * @description: 工作线程01，是一个消费者
 * @date 2021-09-22 23:08:48
 */
public class Worker01 {
    public static final String QUEUE_NAME = "hello";

    // 消费消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        // @FunctionalInterface 下面是一个接口，并且是一个函数式接口，
        DeliverCallback deliverCallback =(consumerTag, message)->{
            System.out.println("接收到的消息：" + new String(message.getBody()));
        };

        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println(consumerTag + "消费者取消消费回调");

        };

        System.out.println("C3等待接收消息！");
        // 消息的接收
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
