package com.wuqr.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 7.7.1.安装延时队列插件
 * 在官网上下载 https://www.rabbitmq.com/community-plugins.html，下载rabbitmq_delayed_message_exchange 插件，然后解压放置到 RabbitMQ 的插件目录。
 * 安装插件，重启rabbitMq
 *
 * // 这个插件开始是上传到 /opt/下的，拷贝到后面的路径下面
 * // 这个插件不会跟着rabbitMQ更新而更新，你看后面路径下面其他插件已经到3.8.8 可以解决延迟队列问题就可以了
 * cp rabbitmq_delayed_messa.0.ez /usr/ge_exchange-3.8lib/rabbitmq/lib/rabbitmq_server-3.8.8/plugins
 *
 * cd /usr/ge_exchange-3.8lib/rabbitmq/lib/rabbitmq_server-3.8.8/plugins
 * // 安装延迟队列插件
 * rabbitmq-plugins enable rabbitmq_delayed_message_exchange
 *
 * // 重启rabbitMQ
 * system restart rabbitmq-server
 *
 * @author wql78
 * @title: DelayedQueueConfig
 * @description: 延迟队列（插件完成） 配置类
 * @date 2021-10-01 17:26:30
 */
@Configuration
public class DelayedQueueConfig {
    public static final String DELAYED_QUEUE_NAME = "delayed.queue";
    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";

    @Bean
    public Queue delayedQueue() {
        return new Queue(DELAYED_QUEUE_NAME);
    }

    // 延迟交换机绑定延迟队列
    @Bean
    public Binding delayedQueueBingdingDelayedExchange(@Qualifier("delayedQueue")Queue delayedQueue,
                                                       @Qualifier("delayedExchange") CustomExchange delayedExchange) {
        return BindingBuilder.bind(delayedQueue).to(delayedExchange).with(DELAYED_ROUTING_KEY).noargs();
    }

    // 声明交换机 么有延迟交换机 就声明一个自定义交换机  安装rabbitmq 延迟队列插件之后 可以使用这种交换机
    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type","direct");
        /**
         * 1. 交换机的名称
         * 2. 交换机的类型
         * 3. 是否需要持久化
         * 4. 是否需要自动删除
         * 5. 其他参数Map
         */
        return new CustomExchange(DELAYED_EXCHANGE_NAME,
                "x-delayed-message", true, false, arguments);
    }
}
