package top.chao58.component.mq;

import lombok.Getter;

@Getter
public enum QueueEnum {

    /*
    流程猜测：
        1、首先拥护下单，然后生成订单id
        2、订单id，放入“准备取消订单”的队列中
            2.1、在倒计时结束前支付，则取出消息，进行处理
            2.2、在倒计时结束后还未支付，则放入“等待取消订单”的队列中
                2.2.3、进行后续的处理
     */
    // 准备取消订单
    QUEUE_ORDER_CANCEL("mall.order.direct", "mall.queue.cancel", "mall.queue.cancel"),
    // 等待取消订单
    QUEUE_TTL_ORDER_CANCEL("mall.ttl.order.direct", "mall.tll.queue.cancel", "mall.tll.queue.cancel");


    private final String exchange;
    private final String queue;
    private final String routingKey;

    QueueEnum(String exchange, String queue, String routingKey) {
        this.exchange = exchange;
        this.queue = queue;
        this.routingKey = routingKey;
    }
}
