package com.zljin.flashbuy.controller;


import com.zljin.flashbuy.model.dto.CreateOrderDTO;
import com.zljin.flashbuy.model.vo.OrderVO;
import com.zljin.flashbuy.model.vo.R;
import com.zljin.flashbuy.service.OrderInfoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<R<OrderVO>> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        return ResponseEntity.ok(R.success(orderInfoService.createOrder(createOrderDTO.getItemId(), createOrderDTO.getPromoId(), createOrderDTO.getAmount())));
    }

}
