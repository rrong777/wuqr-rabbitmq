package com.wuqr.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author wql78
 * @title: DeadLetterQueueConsumer
 * @description:
 *  队列TTL 消费者
 * @date 2021-10-01 15:55:48
 */
@Component
@Slf4j
public class DeadLetterQueueConsumer {
    // 接收消息
    @RabbitListener(queues = "QD")
    public void receiveD(Message message, Channel channel) throws Exception{
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("当前时间： {}, 收到死信队列的消息：{}", new Date().toString(), msg);
    }
}
