package com.wuqr.rabbitmq.controller;

import com.rabbitmq.client.MessageProperties;
import com.wuqr.rabbitmq.config.DelayedQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
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

    // 新的生产者，发送消息的时候带时间,生产的消息自带ttl
    @GetMapping("/sendExpirationMsg/{message}/{ttlTime}")
    public void sendMessage(@PathVariable String message, @PathVariable String ttlTime) {// 方法名可以相同，重载就行
        // {}占位符
        log.info("当前时间：{}，发送一条ttl为{}毫秒的消息给QC ttl队列：{}",
                new Date().toString(), ttlTime, message);

        // messagePostProcessor 设置消息的属性，发送消息方法的第四个参数
        MessagePostProcessor messagePostProcessor = (msg)->{
            // 设置发送消息的延迟市场
            msg.getMessageProperties().setExpiration(ttlTime);
            return msg;
        };

        rabbitTemplate.convertAndSend("X",
                "XC",
                "消息来自QC队列：" + message,
                messagePostProcessor);
    }

    // 插件形成的延时队列
    @GetMapping("/sendDelayMsg/{message}/{delayTime}")
    public void sendMessage(@PathVariable String message, @PathVariable Integer delayTime) {// 方法名可以相同，重载就行
        // {}占位符
        log.info("当前时间：{}，发送一条延时时长为{}毫秒的信息给延时队列delayed.queue：{}",
                new Date().toString(), delayTime, message);



        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANGE_NAME,
                DelayedQueueConfig.DELAYED_ROUTING_KEY,
                message,
                // 发生弄个消息的时候 延迟时长，单位ms
                msg -> {
                    msg.getMessageProperties().setDelay(delayTime);
                    return msg;
                });
    }

    // 开始发消息 测试确认

}
