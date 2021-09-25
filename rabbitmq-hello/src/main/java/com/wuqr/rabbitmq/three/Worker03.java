package com.wuqr.rabbitmq.three;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;
import com.wuqr.rabbitmq.utils.SleepUtils;

/**
 * @author wql78
 * @title: Worker03
 * @description: 消费者2 测试消息在手动应答时是不丢失的，放回队列中重新消费。
 * @date 2021-09-23 22:45:39
 */
public class Worker03 {
    // 队列名称 任务队列
    public static final String TASK_QUEUE_NAME = "ack_queue";
    // 接收消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C2等待接收消息处理，C2等待时间较长");
        DeliverCallback deliverCallback = ((consumerTag, message) -> {
            // 睡眠1秒
            SleepUtils.sleep(10);
            System.out.println("接收到的消息：" + new String(message.getBody(), "UTF-8"));

            // 手动应答

            /**
             * 第一个参数，消息的标记，tag标签
             * 第二个参数，是否批量应答. false 不批量应答
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        });
        CancelCallback cancelCallback = (consumerTag)->{
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
        };
        // 设置不公平分发
        int perfetchCount = 1;
        channel.basicQos(perfetchCount);
        boolean autoAck = false;

        channel.basicConsume(TASK_QUEUE_NAME, autoAck,  deliverCallback, cancelCallback);
    }
}
