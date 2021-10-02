package com.wuqr.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * @author wql78
 * @title: MyCallBack
 * @description:
 * @date 2021-10-02 15:29:25
 * 代码写好后 还要进行配置 更改生产者消息确认模式
 * 在配置文件当中需要添加
 * spring.rabbitmq.publisher-confirm-type=correlated
 * ⚫ NONE
 * 禁用发布确认模式，是默认值
 * ⚫ CORRELATED
 * 发布消息成功到交换器后会触发回调方法
 * ⚫ SIMPLE  同步的，之前已经测试过了 ，发一条确认一条
 */
@Component // 声明成组件 spring会帮我们实例化并且管理起来
@Slf4j
// ConfirmCallback是一个内部接口，实现了内部接口， RabbitTemplate在调用callBack的时候 调用不到我们提供的这个实现类
// 我们需要注入到RabbitTemplate 这个类里面的这个ConfirmCallback 接口的引用上，才会用我们这个实现，不然就用默认的实现了
public class MyCallBack implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 注入rabbitTemplate 的setConfirmCallback
    @PostConstruct // 容器初始化后执行这个，替换默认的callBack实现 PostConstruct注解是在其他注解都完成之后才执行
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this); // 当前类实现了两个函数式接口，所以当前类对象this 在两个函数式接口使用的地方都能使用
    }


    /**
     * 交换机退回消息的方法（发不到队列（无法路由）怎么办）
     * 可以在当消息传递过程中不可达目的地时将消息返回给生产者。
     * 只有无法路由到队列的消息 交换机才会调用这个方法，成功路由的消息就从交换机丢弃了
     * message 消息 replyCode 响应码  replyText 失败的原因 exchange 交换机 routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.error("消息{}，被交换机{}退回，退回原因{}，路由Key{}",
                new String(message.getBody(), StandardCharsets.UTF_8),
                exchange, replyText, routingKey );
    }

    /**
     * 交换机确认回调方法
     * 1. 发消息，交换机接收到了回调
     * 1.1 correlationData 保存回调消息的ID及相关信息
     * 1.2 交换机收到消息的应答 true
     * 1.3 cause null
     * 2. 发消息 交换机接收失败了 回调
     * 2.1  correlationData 保存回调消息的ID及相关信息
     * 2.2 ack = false
     * 2.3 失败的原因
     *
     * correlationData 这个参数是哪里来的，你能收到的有ack 有cause，correlationData回调信息是没有的
     * 是生产者  生产的时候指定好的，rabbitTemplate.convertAndSend 生产者生产消息的方法有大量重载的方法，
     * 有一個如下的会让你声明回调信息
     *     public void convertAndSend(String exchange, String routingKey, Object object, CorrelationData correlationData){
     *         this.send(exchange, routingKey, this.convertMessageIfNecessary(object), correlationData);
     *     }
     * @param s
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";
        if(ack) {
            log.info("交换机已经收到ID为：{}的消息", id);
        } else {
            log.info("交换机还未收到ID为：{}的消息，由于：{}", id, cause);
        }
    }
}
