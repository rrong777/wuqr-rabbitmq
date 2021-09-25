package com.wuqr.rabbitmq.five;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;

/**
 * @author wql78
 * @title: ReceiveLogs01
 * @description: 消息接收者 01
 * @date 2021-09-25 21:13:15
 */
public class ReceiveLogs01 {
    public static final String EXCHANG_NAME = "logs";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 之前消费者指定接收哪个队列的消息，发送者指定往哪个队列里发送消息。 没有指定交换机，就是使用默认的交换机
        // 现在消费者指定从哪个交换机接收消息，并且生成一个队列和交换机进行绑定（交换机通过routingKey和queue绑定）
        // 声明一个交换机
        channel.exchangeDeclare(EXCHANG_NAME, "fanout");
        // 声明一个队列 临时队列
        /**
         * 生成一个临时队列
         * 队列名称随机
         * 消费者断开与队列连接时，队列就自动删除了
         * 这个方法返回的是临时队列的名称 也是随机生成的
         */
        String queueName = channel.queueDeclare().getQueue();
        /**
         * 队列和交换机进行绑定
         * routingKey就是一个空串
         */
        channel.queueBind(queueName, EXCHANG_NAME, "");
        System.out.println("ReceiveLogs01等待接收消息，把接收到的消息打印再屏幕……");

        // 接收消息的回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("ReceiveLogs01控制台打印接收到的消息：" + new String(message.getBody(), "UTF-8"));
        };
        // 取消接收消息的回调 如果不想写 可以传一个空实现

        // 接收消息 basicConsume() basicPublish() 这些方法所有模式都可以使用，不不限简单模式（队列）或者工作模式（队列）
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
}
