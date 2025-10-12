package com.zljin.flashbuy.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderVO {

    /**
     *
     */
    private String id;
    /**
     *
     */
    private String userId;
    /**
     *
     */
    private String itemId;
    /**
     *
     */
    private BigDecimal itemPrice;
    /**
     *
     */
    private Integer amount;
    /**
     *
     */
    private BigDecimal orderPrice;
    /**
     *
     */
    private String promoId;
}
