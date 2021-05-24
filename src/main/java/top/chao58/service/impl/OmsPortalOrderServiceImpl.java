package top.chao58.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chao58.component.mq.CancelOrderSender;
import top.chao58.service.OmsPortalOrderService;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService {

    @Autowired
    private CancelOrderSender cancelOrderSender;


    @Override
    public void generatorOder(Long orderId) {
        log.info("生成订单：{}", orderId);
        try {
            // 模拟订单花费时间
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 异步处理非订单的其他业务逻辑
        sendMessageCancelOrder(orderId);
    }

    private void sendMessageCancelOrder(Long orderId) {
        // 超时时间（毫秒）
        long delayTime = 1000 * 30;
        cancelOrderSender.sendMessage(orderId, delayTime);
        log.info("发送订单id；{}", orderId);
    }

    @Override
    public void cancelOrder(Long orderId) {
        log.error("订单取消：{}", orderId);
    }
}
