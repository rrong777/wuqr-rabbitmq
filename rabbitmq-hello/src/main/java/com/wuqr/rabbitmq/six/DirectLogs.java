package com.wuqr.rabbitmq.six;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author wql78
 * @title: DirectLogs
 * @description: @TODO
 * @date 2021-09-25 21:51:07
 */
public class DirectLogs {
    public static final String EXCHANG_NAME = "direct_logs";
    public static void main(String[] args) throws Exception {

        Channel channel = RabbitMqUtils.getChannel();
        // 消费者先启动，已经声明了交换机，这里就不声明了
        // 其实应该是在生产者声明交换机，绑定队列，然后在消费者声明队列，然后取数据
//        channel.exchangeDeclare(EXCHANG_NAME, BuiltinExchangeType.DIRECT);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish(EXCHANG_NAME, "info", null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息：" + message);

        }
    }
}
