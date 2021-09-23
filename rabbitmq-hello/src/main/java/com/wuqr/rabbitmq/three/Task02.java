package com.wuqr.rabbitmq.three;

import com.rabbitmq.client.Channel;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author wql78
 * @title: Task02
 * @description: 生产者，测试消息在手动应答时是不丢失的，放回队列中重新消费。
 *  本次测试的是消费者，跟生产者
 * @date 2021-09-23 22:37:42
 */
public class Task02 {
    // 队列名称 任务队列
    public static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 声明队列 其实就是去创建队列，
        channel.queueDeclare(TASK_QUEUE_NAME, false, false ,false, null);
        // 从控制台输入消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            // 如果你传入的message是英文 msg.getBytes()；即可，如果传入的有中文，msg.getBytes("UTF-8")一定要带字符集
            channel.basicPublish("", TASK_QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息：" + message);
        }
    }
}
