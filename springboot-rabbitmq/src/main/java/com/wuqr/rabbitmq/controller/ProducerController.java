package com.wuqr.rabbitmq.controller;

import com.wuqr.rabbitmq.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
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
        // 这个对象有两个参数，id 还有消息背身
        // 这个对象是回调信息，发送消息的时候要带上，回调才能收到
        CorrelationData correlationData = new CorrelationData("1");

        // 提供一个错误的交换机名字，测试消息发不出去的时候 调用回调
        // channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'confirm_exchange123' in vhost '/', class-id=60, method-id=40)
        // 修改后 发送失败 调用回调  错误原因，没有找到交换机confirm_exchange123 可以在回调中存储消息 以后重发
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY,
                message,correlationData);
        log.info("发送消息内容： {}", message);

        CorrelationData correlationData2 = new CorrelationData("2");

        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE_NAME,
                ConfirmConfig.CONFIRM_ROUTING_KEY + "2", // 错误的队列名称，交换机正常，队列无法正常收到消息
                message,correlationData2);
        log.info("发送消息内容： {}", message);
    }
}
