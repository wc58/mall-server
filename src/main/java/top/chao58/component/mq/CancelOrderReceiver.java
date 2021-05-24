package top.chao58.component.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.chao58.service.OmsPortalOrderService;

@Slf4j
@Component
@RabbitListener(queues = "mall.queue.cancel")
public class CancelOrderReceiver {


    @Autowired
    private OmsPortalOrderService omsPortalService;

    /**
     * 只有当订单id超时时，才会出现在该队列中，然后对订单做一系列操作
     *
     * @param orderId 超时的订单id
     */
    @RabbitHandler
    public void handler(Long orderId) {
        log.warn("订单【{}】超时，准备后续处理", orderId);
        omsPortalService.cancelOrder(orderId);
    }

}
