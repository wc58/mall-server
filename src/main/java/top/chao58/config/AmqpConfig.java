package top.chao58.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.chao58.component.mq.QueueEnum;

import java.util.HashMap;

@Configuration
public class AmqpConfig {

    // 准备取消订单
    private final QueueEnum queueOrderCancel = QueueEnum.QUEUE_ORDER_CANCEL;
    // 等待取消订单
    private final QueueEnum queueTtlOrderCancel = QueueEnum.QUEUE_TTL_ORDER_CANCEL;

    @Bean
    public DirectExchange orderDirect() {
        return ExchangeBuilder.directExchange(queueOrderCancel.getExchange()).durable(true).build();
    }

    @Bean
    public DirectExchange orderTtlDirect() {
        return ExchangeBuilder.directExchange(queueTtlOrderCancel.getExchange()).durable(true).build();
    }

    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(queueOrderCancel.getQueue()).build();
    }

    @Bean
    public Queue orderTtlQueue() {
        HashMap<String, Object> arguments = new HashMap<>();
        // 过期重定向,-> ” 准备取消订单“的队列中
        arguments.put("x-dead-letter-exchange", queueOrderCancel.getExchange());
        // 重定向所带的key
        arguments.put("x-dead-letter-routing-key", queueOrderCancel.getRoutingKey());
        return QueueBuilder.durable(queueTtlOrderCancel.getQueue())
                .withArguments(arguments)
                .build();
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderDirect()).with(queueOrderCancel.getRoutingKey());
    }

    @Bean
    public Binding orderTtlBinding() {
        return BindingBuilder.bind(orderTtlQueue()).to(orderTtlDirect()).with(queueTtlOrderCancel.getRoutingKey());
    }

}
