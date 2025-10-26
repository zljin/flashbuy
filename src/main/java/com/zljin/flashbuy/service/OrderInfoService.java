package com.zljin.flashbuy.service;

import com.zljin.flashbuy.model.vo.OrderVO;

/**
* @author zoulingjin
* @description 针对表【order_info(订单表)】的数据库操作Service
* @createDate 2025-10-06 11:19:04
*/
public interface OrderInfoService {

    OrderVO createOrder(String itemId, String promoId, Integer amount);
}
