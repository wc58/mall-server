package top.chao58.component.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelOrderSender {

    private final QueueEnum queueTtlOrderCancel = QueueEnum.QUEUE_TTL_ORDER_CANCEL;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送给mq，做异步处理，设置超时时间
     *
     * @param orderId   订单id
     * @param delayTime 超时时间
     */
    public void sendMessage(Long orderId, final long delayTime) {
        amqpTemplate.convertAndSend(queueTtlOrderCancel.getExchange(), queueTtlOrderCancel.getRoutingKey(), orderId, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 给消息设置超时时间
                message.getMessageProperties().setExpiration(String.valueOf(delayTime));
                return message;
            }
        });
        log.info("向【{}】交换机，发送：{}，超时时间；{}", queueTtlOrderCancel.getExchange(), orderId, delayTime);
    }


}
