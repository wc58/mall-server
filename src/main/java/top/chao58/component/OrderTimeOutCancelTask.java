package top.chao58.component;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class OrderTimeOutCancelTask {

    @Scheduled(cron = "0 0/10 * * * ?")
    private void cancelOrderTimeOut() {
        log.info("取消超时订单");
    }


}
