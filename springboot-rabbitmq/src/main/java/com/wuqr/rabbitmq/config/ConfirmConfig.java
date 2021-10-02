package com.wuqr.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wql78
 * @title: ConfirmConfig
 * @description: 发布确认（高级）配置
 * @date 2021-10-02 13:58:18
 */
@Configuration
public class ConfirmConfig {
    // 交换机
    public static final String CONFIRM_EXCHANGE_NAME = "confirm_exchange";// broker中的交换机一旦重名就会抛异常
    // 队列
    public static final String CONFIRM_QUEUE_NAME = "confirm_queue";
    // routingKey
    public static final String CONFIRM_ROUTING_KEY = "key1";

    // 声明交换机
    @Bean
    public DirectExchange confirmExchange(){
        return new DirectExchange(CONFIRM_EXCHANGE_NAME);
    }
    // 声明队列
    @Bean
    public Queue confirmQueue(){
//        return new Queue(CONFIRM_QUEUE_NAME); // 下面这种写法是一样的
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    // 绑定
    @Bean
    public Binding queueBindingExchange(@Qualifier("confirmQueue") Queue confirmQueue,
                                        @Qualifier("confirmExchange") DirectExchange confirmExchange) {
        return BindingBuilder.bind(confirmQueue).to(confirmExchange).with(CONFIRM_ROUTING_KEY);
    }
}
