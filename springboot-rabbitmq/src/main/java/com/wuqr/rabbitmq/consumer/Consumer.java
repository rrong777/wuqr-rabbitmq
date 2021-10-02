package com.wuqr.rabbitmq.consumer;

import com.wuqr.rabbitmq.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author wql78
 * @title: Consumer
 * @description: 接收消息，
 * @date 2021-10-02 14:12:19
 */
@Component
@Slf4j
public class Consumer {
    @RabbitListener(queues = ConfirmConfig.CONFIRM_QUEUE_NAME)
    public void receiveConfirmMsg(Message msg) {
        String message = new String(msg.getBody(), StandardCharsets.UTF_8);
        log.info("接收到对垒confirm.queue消息： {}", message);
    }
}
