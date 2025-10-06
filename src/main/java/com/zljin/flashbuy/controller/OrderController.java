package com.zljin.flashbuy.controller;


import com.zljin.flashbuy.model.vo.OrderVO;
import com.zljin.flashbuy.model.vo.R;
import com.zljin.flashbuy.service.OrderInfoService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Validated
public class OrderController {

    private final OrderInfoService orderInfoService;

    public OrderController(OrderInfoService orderInfoService) {
        this.orderInfoService = orderInfoService;
    }

    @PostMapping("/create")
    public ResponseEntity<R<OrderVO>> createOrder(@RequestParam(name = "itemId") Long itemId,
                                                  @RequestParam(name = "amount")
                                                  @Min(value = 1, message = "购买数量至少为1")
                                                  @Max(value = 100, message = "购买数量不能超过100") Integer amount,
                                                  @RequestParam(name = "promoId", required = false) Long promoId) {
        return ResponseEntity.ok(R.success(orderInfoService.createOrder(itemId, promoId, amount)));
    }

}
