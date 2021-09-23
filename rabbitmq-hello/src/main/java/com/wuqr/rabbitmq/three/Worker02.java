package com.wuqr.rabbitmq.three;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;
import com.wuqr.rabbitmq.utils.SleepUtils;

/**
 * @author wql78
 * @title: Worker02
 * @description: 消费者1 测试消息在手动应答时是不丢失的，放回队列中重新消费。
 * @date 2021-09-23 22:45:29
 */
public class Worker02 {
    // 队列名称 任务队列
    public static final String TASK_QUEUE_NAME = "ack_queue";
    // 接收消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C1等待接收消息处理，C1等待时间较短");
        // 函数式接口，入参固定的，所以可以直接用lambda表达式
        DeliverCallback deliverCallback = ((consumerTag, message) -> {
            // 到这里回调调用了，就已经收到消息了
            // 睡眠1秒
            SleepUtils.sleep(1);
            // 如果有中文，new 的时候要指定字符集。 你那边传过来的解码字符集是根据utf-8  我这里编码字符集也要是utf-8
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
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }
}
