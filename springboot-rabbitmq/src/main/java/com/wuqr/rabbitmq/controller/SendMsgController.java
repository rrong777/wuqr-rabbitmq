package com.wuqr.rabbitmq.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author wql78
 * @title: SendMsgController
 * @description: 发送延迟消息
 *
 * http://localhostL8080/ttl/sendMsg/嘻嘻嘻
 * @date 2021-10-01 15:04:49
 */
@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    // 开始发送消息
    @GetMapping("/sendMsg/{message}")
    public void sendMessage(@PathVariable String message) {
        // {}占位符
        log.info("当前时间：{}，发送一条信息给两个ttl队列：{}", new Date().toString(), message);
        rabbitTemplate.convertAndSend("X", "XA", "消息来自TTL为10S的队列：" + message);
        rabbitTemplate.convertAndSend("X", "XB", "消息来自TTL为40S的队列：" + message);
    }

    // 生产者写完该写消费者  消费者通过监听方式， 
    //    http://localhost:8080/ttl/sendMsg/嘻嘻嘻，测试地址 往这里发送一次请求，会生产一条消息，发往两个延迟队列

}
