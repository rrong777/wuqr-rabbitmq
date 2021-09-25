package com.wuqr.rabbitmq.four;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.wuqr.rabbitmq.utils.RabbitMqUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author wql78
 * @title: ConfirmMessage
 * @description: 发布确认默认 使用的时间比较那种确认方式最好
 * 1. 单个确认模式
 * 2. 批量确认模式
 * 2. 异步批量确认模式
 * @date 2021-09-25 16:47:51
 */
public class ConfirmMessage {
    // 批量发送消息的个数
    public static final int MESSAGE_COUNT = 1000;
    public static void main(String[] args) throws Exception {
        // 1. 单个确认
//        publicMessageIndividually(); // 发布1000个单独确认消息，耗时682
        // 2. 批量确认 // 发布1000个批量确认消息，耗时141
//        publishMessageBatch();
        // 3. 异步批量确认
        publishMessageAsync(); // 发布1000个异步发布确认消息，耗时46 由于是异步确认，所以控制台还在打印确认接收消息的回调日志的时候，这里就已经1000条确认了
    }
    // 单个确认
    public static void  publicMessageIndividually() throws Exception {
        // 第一步毋庸置疑的肯定是获取信道，你不管消费还是生产消息，都要获取信道
        Channel channel = RabbitMqUtils.getChannel();
        // 队列的声明 uuid长度是36位的
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        // 开启发布确认
        channel.confirmSelect();
        // 开始时间
        long begin = System.currentTimeMillis();
        // 批量发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            // 单个消息就马上进行发布确认；
            boolean flag = channel.waitForConfirms();
            if(flag == true) {
                System.out.println("消息发送成功！");
            }
        }
        // 结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息，耗时" + (end - begin));
    }
    // 批量发布确认
    public static void publishMessageBatch() throws Exception {
        // 第一步毋庸置疑的肯定是获取信道，你不管消费还是生产消息，都要获取信道
        Channel channel = RabbitMqUtils.getChannel();
        // 队列的声明 uuid长度是36位的
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        // 开启发布确认
        channel.confirmSelect();
        // 开始时间
        long begin = System.currentTimeMillis();
        // 批量确认消息数量
        int batchSize = 100;
        // 批量发送消息 批量发布确认
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", queueName, null, message.getBytes());
            // 判断批次发送消息是否达到100条，批量确认一次
            if((i+1) % batchSize == 0) {
                // 单个消息就马上进行发布确认；
                boolean flag = channel.waitForConfirms();
                if(flag == true) {
                    System.out.println("批量消息发送成功！");
                }
            }
        }

        // 结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息，耗时" + (end - begin));
    }
    // 异步发布确认
    public static void publishMessageAsync() throws Exception {
        // 如下的边缘代码都是一致的
        // 第一步毋庸置疑的肯定是获取信道，你不管消费还是生产消息，都要获取信道
        Channel channel = RabbitMqUtils.getChannel();
        // 队列的声明 uuid长度是36位的
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, false, false, false, null);
        // 开启发布确认
        channel.confirmSelect();

        /**
         *  线程安全 有序的一个哈希表（Map） 适用于高并发的情况下
         *  外部确认的容器
         *  1. 轻松的将序号与消息进行关联（map）
         *  2. 轻松的批量删除条目 只要给到序号（key）
         *  3. 支持高并发（多线程，重点）
         */
        ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
        // 开始时间
        long begin = System.currentTimeMillis();
        // 消息确认成功回调函数
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            System.out.println("确认的消息：" + deliveryTag);
            // 2. 删除已经确认的消息，剩下的就是未确认的消息
            if(multiple) {
                // 在头部的map 如果是批量发送消息的，确认也是批量的，也就是说批量发送，一次发送十条，确认的是小于这个key的
                // 都是确认发送的消息，headMap 小于deliveryTag的全部就是确认过的，清除掉。headMap是浅拷贝
                // 批量发送消息的话，一批次只有一次确认，确认最后一条，所以最后一条之前的map全部删除
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag);
                confirmed.clear();
            } else {
                // 如果不是批量，删除当前消息即可
                outstandingConfirms.remove(deliveryTag);
            }
        };
        /**
         * 第一个参数： 消息的标记
         * 第二个参数： 是否为批量确认
         */
        // 消息确认失败回调函数
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            // 3. 打印一下未确认的消息有哪些
            String message = outstandingConfirms.get(deliveryTag);
            System.out.println("未确认的消息是" + message + "未确认的消息的标记：" + deliveryTag);
//            deliveryTag 就是消息的标识
        };

        /**
         * 第一个参数 监听哪些消息成功了
         * 第二个参数 监听哪些消息失败了
         */
        // 单参数和多参数的监听器，单参数表示只监听成功的，两个参数多参的表示成功和失败的一起监听
        // 需要声明两个函数式接口
        // 这个监听器是异步的，消息发送半天了，这个监听器才会被触发，消息发完了，监听还在不断触发，也就是说发送消息，和消息确认这个监听器两个并不是同步的，
        // 所以你会发现最后发布1000个消息所用耗时打印日志的时候，是在消息确认的中间，1000条先发完，然后还在执行其他消息发布确认的监听
        // 打印 发布1000个异步发布确认消息，耗时xx的时候，其实消息就已经发送完毕了。后续的日志其实就不是在发送消息了，只是在执行
        // 监听消息发布确认的监听器了。由于监听器是从你开始发送消息，到你发送完消息，异步执行的，所以我要收集未确认的消息，
        // 或重新发，或保存下来之后重新往队列发
        // 多线程，从上往下走，走到这里的时候监听器并没有执行，在这里会开启一个多线程去执行监听器。
        // 即使当前线程执行完了 这个监听器也有很大可能没执行完。一个线程负责监听，一个线程负责发送消息
        // 如果有未确认的消息，在两个线程之间传递，你只能用类似ConcurrentLinkedQueue这种并发链路式队列在两个线程之间进行消息传递
        channel.addConfirmListener(ackCallback, nackCallback);

        // 批量发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "消息" + i;
            channel.basicPublish("", queueName, null, message.getBytes());
            // 你尽管发，确认的事情不由你完成，由Broker完成
            // broker会通过信道返回回来，通知你这个发件人，哪些收到了哪些你需要重发，你等着就需要有一个监听器，发消息前就准备好
            // 因为broker不一定什么时候给你响应，你需要监听哪些消息成功 哪些消息失败  开始发送消息前监听器就要就位，因为你不
            // 知道什么时候会响应你 给你确认
            // 1. 此处记录下所有要发送的消息 消息的总和, 每发送一条消息就记录一次。
            outstandingConfirms.put(channel.getNextPublishSeqNo(), message);

        }
        
        // 结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个异步发布确认消息，耗时" + (end - begin));
    }
}
