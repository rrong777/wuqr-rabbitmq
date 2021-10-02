package com.wuqr.rabbitmq.consumer;

import com.wuqr.rabbitmq.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author wql78
 * @title: DelayedQueueConsumer
 * @description: 消费者 消费基于插件的延迟消息
 * @date 2021-10-02 00:59:29
 */
@Component
@Slf4j
public class DelayedQueueConsumer {
    @RabbitListener(queues = DelayedQueueConfig.DELAYED_QUEUE_NAME) // 监听消息
    public void receiveDelayQueue(Message message){
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("当前时间：{}，收到延迟队列的消息：{}" ,new Date().toString(), msg);
    }

}
