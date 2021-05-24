package top.chao58.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.chao58.service.OmsPortalOrderService;
import top.chao58.util.R;

@Api(tags = "订单管理")
@RequestMapping("/order")
@RestController
public class OmsPortalOrderController {

    @Autowired
    private OmsPortalOrderService portalOrderService;


    @ApiOperation("下单操作")
    @PostMapping("/generator")
    public R generatorOrder(@Parameter(description = "订单id") @RequestParam Long orderId) {
        portalOrderService.generatorOder(orderId);
        return R.ok();
    }

}
