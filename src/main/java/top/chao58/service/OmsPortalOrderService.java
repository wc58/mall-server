package top.chao58.service;

public interface OmsPortalOrderService {

    //todo-chao 2021/5/23 20:22：暂时传入一个id，模拟信息，现在主要测试mq是否正常
//    @Transactional
    void generatorOder(Long orderId);

    void cancelOrder(Long orderId);

}
