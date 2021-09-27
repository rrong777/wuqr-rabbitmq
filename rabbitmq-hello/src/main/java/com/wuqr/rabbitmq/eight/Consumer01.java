package com.wuqr.rabbitmq.eight;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;
import com.wuqr.rabbitmq.utils.SleepUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wql78
 * @title: Consumer01
 * @description: 死信队列实战
 * 消费者01
 * @date 2021-09-27 22:28:36
 */
public class Consumer01 {
    // 普通交换机名称
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    // 死信交换机名称
    public static final String DEAD_EXCHANGE = "dead_exchange";
    // 普通队列名称
    public static final String NORMAL_QUEUE = "normal_queue";
    // 死信队列名称
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 声明普通和死信交换机 类型为direct
        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        // 声明普通队列
        Map<String, Object> arguments = new HashMap<>();
        // 过期时间 不设置过期时间
//        arguments.put("x-message-ttl", 10000);
        // 正常队列应该设置死信交换机是谁，死信交换机其实也是普通交换机，只是说普通队列中的消息因为某些原因，导致这些消息变成死信
        // 当前普通队列声明这些原因，并且指明一个交换机，然后把当前队列这些死信暂存到另一个队列（先发往另一个交换机），死信交换机
        // 就是用来接收死信和发出死信的，死信队列就是用来存死信的普通队列，名称只是我们加上了不同的意义
        arguments.put("x-dead-letter-exchange", DEAD_EXCHANGE);// 指定死信交换机，一旦当前队列所连接的
        // 消费者不能正常消费消息，马上把消息转发到死信交换机， 死信往这个交换机发送

        // 设置死信routingKey， 死信交换机和死信队列相当于外挂在普通队列上的一个额外存储，这个存储应该可以多个队列共同使用
        arguments.put("x-dead-letter-routing-key", "lisi");// 死信往上面的交换机中的lisi队列发送
        arguments.put("x-max-length", 6);// 队列最大长度
        channel.queueDeclare(NORMAL_QUEUE, false, false, false, arguments);




        // 声明死信队列 死信队列就是一个普通的队列，上面的普通队列，往死信交换机发送消息的，倒是和普通队列有些不同，要加一些参数
        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);

        // binding 普通交换机和普通队列进行绑定 建立绑定关系
        channel.queueBind(NORMAL_QUEUE, NORMAL_EXCHANGE, "zhangsan");
        // binding 死信交换机和死信队列进行绑定
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, "lisi");// arguments只是声明lisi队列的死信往这边发。
        // 这边还是要建立队列和交换机之间的绑定


        System.out.println("等待接收消息…………");

        // 消息因为某些原因 变成死信之后，是由普通队列转发给死信队列的。 需要设置一定参数 普通队列才能把自己的消息转发给死信交换机
        // 再由死信交换机转发给死信队列
        DeliverCallback deliverCallback = (consumerTag, message ) -> {
            System.out.println("Consumer01接收的消息是：" + new String(message.getBody(), StandardCharsets.UTF_8));
        };
        channel.basicConsume(NORMAL_QUEUE, true, deliverCallback, consumerTag -> {});
    }

}
