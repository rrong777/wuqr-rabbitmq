package com.wuqr.rabbitmq.five;

import com.rabbitmq.client.Channel;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author wql78
 * @title: EmitLog
 * @description: 消息生产者 发消息给交换机
 * @date 2021-09-25 21:13:02
 */
public class EmitLog {
    public static final String EXCHANG_NAME = "logs";
    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();
        // 声明交换机。可以多次声明，也可以只声明一次，先运行的地方声明了，如果是消费者先运行，并且已经声明交换机，下面不声明也可以
        channel.exchangeDeclare(EXCHANG_NAME, "fanout");// fanout 扇出模式的交换机
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            // 原来交换机这个参数传的就是空串。  第二个参数 消费者指定logs交换机绑定的两个队列的routingKey是空串，这里也就传空串
            channel.basicPublish(EXCHANG_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息：" + message);// 然后就可以启动消费者等待接收消息
            // 你会发现这里生产的消息发送到logs交换机中，并且找到routingKey为空串的两个队列，都发送过去了。
        }
    }
}
