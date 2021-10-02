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
    // 备份交换机
    public static final String BACKUP_EXCHANGE_NAME = "backup_exchange";// broker中的交换机一旦重名就会抛异常
    // 备份队列
    public static final String BACKUP_QUEUE_NAME = "backup_queue";
    // 告警队列
    public static final String WARNING_QUEUE_NAME = "warning_queue";

    // 声明交换机
    @Bean
    public DirectExchange confirmExchange(){
//        return new DirectExchange(CONFIRM_EXCHANGE_NAME);
        DirectExchange confirmExchange = ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME).durable(true)
                .withArgument("alternate-exchange", BACKUP_EXCHANGE_NAME).build();// 参数指向备份交换机，这个交换机无法接收到消息的时候就指向备份交换机
        return confirmExchange;
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

    // 备份交换机创建 备份交换机 扇出模式，因为出问题的消息要同时发送到两个队列
    @Bean
    public FanoutExchange backupExchange() {
        return new FanoutExchange(BACKUP_EXCHANGE_NAME);
    }

    // 备份队列
    @Bean
    public Queue backupQueue(){
        return QueueBuilder.durable(BACKUP_QUEUE_NAME).build();
    }

    // 告警队列
    @Bean
    public Queue warningQueue(){
        return QueueBuilder.durable(WARNING_QUEUE_NAME).build();
    }

    // 绑定备份交换机和备份队列绑定
    @Bean
    public Binding backupQueueBindingBackupExchange(@Qualifier("backupQueue") Queue backupQueue,
                                        @Qualifier("backupExchange") FanoutExchange backupExchange) {
        return BindingBuilder.bind(backupQueue).to(backupExchange); // 扇出交换机的routingKey没有意义 所以不用.with(routingKey)
    }
    // 绑定备份交换机和警队列绑定
    @Bean
    public Binding warningQueueBindingBackupExchange(@Qualifier("warningQueue") Queue warningQueue,
                                        @Qualifier("backupExchange") FanoutExchange backupExchange) {
        return BindingBuilder.bind(warningQueue).to(backupExchange);
    }
}
