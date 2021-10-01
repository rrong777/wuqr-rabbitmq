package com.wuqr.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wql78
 * @title: TtlQueueConfig
 * @description: ttl队列 配置文件类代码
 * @date 2021-10-01 13:44:34
 */
@Configuration
public class TtlQueueConfig {
    // 普通交换机的名称
    public static final String X_EXCHANGE = "X";
    // 死信交换机名称
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";

    // 普通队列名称
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";

    // 死信队列名称
    public static final String DEAD_LETTER_QUEUE = "QD";

    // 声明xExchange; xExchange 别名，  autowired的时候用，在容器中管理的就是这个名字就是这个方法返回的对象
    @Bean("xExchange")
    public DirectExchange xExchange() {
        return new DirectExchange(X_EXCHANGE);
    }

    // 死信交换机
    @Bean("yExchange")
    public DirectExchange yExchange() {
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    // 声明普通队列 普通队列A 过期时间为10S
    @Bean("queueA")
    public Queue queueA() {
        // 队列参数
        Map<String, Object> arguments = new HashMap<>(3); // 提升效率，我们知道这个map装3个
        // 声明的时候长度声明好就可以了
        arguments.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);// 死信交换机，过期消息往死信交换机推
        arguments.put("x-dead-letter-routing-key", "YD");// 死信routingKey， 死信交换机往哪个队列推，那个队列就是死信队列
        arguments.put("x-message-ttl", 10000);//TTL 消息存活时间  ms
        return QueueBuilder.durable(QUEUE_A) // 持久化的队列 queueA
                .withArguments(arguments).build(); // 创建普通队列a
    }
    // 两个普通队列声明方式几乎一模一样
    @Bean("queueB")
    public Queue queueB() {
        Map<String, Object> arguments = new HashMap<>(3);
        arguments.put("x-dead-letter-exchange", Y_DEAD_LETTER_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", "YD");
        arguments.put("x-message-ttl", 40000);
        return QueueBuilder.durable(QUEUE_B)
                .withArguments(arguments).build();
    }

    // 死信队列
    @Bean("queueD")
    public Queue queueD() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    // 执行Binding工作的bean 不需要声明别名，因为不需要调用 其实上面的都不需要别名的，方法名就是别名
    // 普通队列A绑定普通交换机X
    @Bean   // @Qualifier 在入参的时候通过对象在容器中的名称 从容器中找到进行入参
    public Binding ququeABindingX(@Qualifier("queueA") Queue queueA, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    // 普通队列B和X交换机进行绑定
    @Bean
    public Binding ququeBBindingX(@Qualifier("queueB") Queue queueB, @Qualifier("xExchange") DirectExchange xExchange) {
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }

    // 死信队列D和死信交换机Y进行绑定
    @Bean
    public Binding queueDBindingX(@Qualifier("queueD") Queue queueD, @Qualifier("yExchange") DirectExchange yExchange) {
        return BindingBuilder.bind(queueD).to(yExchange).with("YD");
    }
}
