package org.xiangqian.monolithic.biz.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangqian.monolithic.model.Resp;

/**
 * @author xiangqian
 * @date 21:35 2023/03/28
 */
//@RestController
@RequestMapping("/order")
@Tag(name = "订单接口", description = "订单相关接口")
public class OrderController {

    @GetMapping("/test")
    @Operation(summary = "获取订单数据")
    public Resp<?> test() throws Exception {
        return null;
    }

}
