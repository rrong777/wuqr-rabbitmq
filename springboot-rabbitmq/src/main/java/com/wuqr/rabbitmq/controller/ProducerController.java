package com.wuqr.rabbitmq.controller;

import com.wuqr.rabbitmq.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wql78
 * @title: ProducerController
 * @description: 开始发消息 测试确认（这里确认是确认生产者成功发送消息，之前是确认消费者成功消费消息）
 * @date 2021-10-02 14:08:29
 */
@RestController
@RequestMapping("/confirm")
@Slf4j
public class ProducerController {
    @Autowired
    private RabbitTemplate rabbitTemplate; // 注入一个rabbitTemplate;
    @GetMapping("/sendMesssage/{message}")
    public void sendMessage(@PathVariable String message){
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME, ConfirmConfig.CONFIRM_ROUTING_KEY,
                message);
        log.info("发送消息内容： {}", message);
    }
}
