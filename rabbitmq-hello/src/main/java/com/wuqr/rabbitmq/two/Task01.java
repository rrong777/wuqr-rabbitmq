package com.wuqr.rabbitmq.two;

import com.rabbitmq.client.Channel;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;

import java.util.Scanner;

/**
 * @author wql78
 * @title: Task01
 * @description: @TODO
 * @date 2021-09-23 19:56:44
 * 生产者 发送大量消息
 */
public class Task01 {
    // 队列名称
    public static final String QUEUE_NAME = "hello";

    // 发送消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false ,null);
        // 从控制台当中接收消息，控制台输入什么消息，他就发送什么消息
        Scanner scanner = new Scanner(System.in); // 扫描你控制台输入的内容
        while (scanner.hasNext()) {
            String message = scanner.next();
            // 往生产者中推送消息
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("发送消息完成：" + message);
        }
    }
}
