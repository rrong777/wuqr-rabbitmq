package com.wuqr.rabbitmq.consumer;

import com.wuqr.rabbitmq.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author wql78
 * @title: WarningConsumer
 * @description: 告警消费者
 *
 * @date 2021-10-02 16:59:24
 */
@Component
@Slf4j
public class WarningConsumer {
    // 接受告警消息 本来还要提供一个备份消费者 消费备份队列里面的消息，但是这里为了简单就不提供了  告警队列和告警消费者都收到了
    // 备份队列和备份消费者一样可以 去rabbitMQ控制台把原来的交换机删了，因为原来的已经改了一些参数了
    @RabbitListener(queues = ConfirmConfig.WARNING_QUEUE_NAME)
    public void receiveWaringMessage(Message message) {

        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.error("报警发现不可路由消息： {}", msg);
    }
}
