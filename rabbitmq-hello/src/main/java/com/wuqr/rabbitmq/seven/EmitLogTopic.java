package com.wuqr.rabbitmq.seven;

import com.rabbitmq.client.Channel;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wql78
 * @title: EmitLogTopic
 * @description: 发送日志往主题交换机
 * @date 2021-09-25 23:21:16
 */
public class EmitLogTopic {
    // 交换机名称
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        // 第一步 毋庸置疑，获取信道，信道，消息的通道
        Channel channel = RabbitMqUtils.getChannel();
        /**
         * Q1-->绑定的是
         *  中间带 orange 带 3 个单词的字符串(*.orange.*)
         * Q2-->绑定的是
         *  最后一个单词是 rabbit 的 3 个单词(*.*.rabbit)
         *  第一个单词是 lazy 的多个单词(lazy.#)
         */
        Map<String, String> bindingKeyMap = new HashMap<>();
        bindingKeyMap.put("quick.orange.rabbit", "被队列 Q1Q2 接收到");
        bindingKeyMap.put("lazy.orange.elephant", "被队列 Q1Q2 接收到");
        bindingKeyMap.put("quick.orange.fox", "被队列 Q1 接收到");
        bindingKeyMap.put("lazy.brown.fox", "被队列 Q2 接收到");
        bindingKeyMap.put("lazy.pink.rabbit", "虽然满足队列2的两个routingKey绑定但只被队列 Q2 接收一次");
        bindingKeyMap.put("quick.brown.fox", "不匹配任何绑定不会被任何队列接收到会被丢弃");
        bindingKeyMap.put("quick.orange.male.rabbit", "是四个单词不匹配任何绑定会被丢弃");
        bindingKeyMap.put("lazy.orange.male.rabbit", "是四个单词但匹配 Q2");

        // 循环遍历Map 往指定key发送消息
        for (String routingKey : bindingKeyMap.keySet()) {
            String message = bindingKeyMap.get(routingKey);
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息：" + bindingKeyMap.get(routingKey));
        }
    }
}
