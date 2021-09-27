package com.wuqr.rabbitmq.eight;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;
import com.wuqr.rabbitmq.utils.SleepUtils;

/**
 * @author wql78
 * @title: Producer
 * @description: 死信队列实战
 * 生产者代码,生产者只负责发送消息给普通交换机即可，他根本不需要知道后面队列关于死信的操作，我只管发就行了，
 * 死信是在后面队列里面的概念
 * @date 2021-09-27 22:50:17
 */
public class Producer {
    // 普通交换机名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 死信消息 设置TTL时间 ttl： time to live 单位是毫秒，发送消息的时候就要给消息设置过期时间，队列只是暂存，生产者才能定义
        // 消息的存活时间
//        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
        // 本次测试队列达到最大长度之后造成的死信，不再设置消息过期时间
        // 删除队列后打开消费者01重新创建队列和交换机，然后关闭消费者01，关闭消费者01并不代表把队列删除了，此时生产者生产10条消息
        // 6条会放到普通队列，此时普通队列已经满了，剩下四条会被放到死信队列
        for (int i = 1; i < 11; i++) {

            String message = "info" + i;
            channel.basicPublish(NORMAL_EXCHANGE, "zhangsan", null, message.getBytes());
            SleepUtils.sleep(1);
        }
        // 先启动C1 ，创建普通、死信交换机，普通、死信队列，然后把C1（消费者01） 关闭伪装宕机， 然后启动生产者，往普通交换机
        // 发送消息，队列已经有了，但是C1挂了没人消费，超过时间之后消息就被转发到死信队列，由死信队列连接的消费者C2进行消费
    }
}
