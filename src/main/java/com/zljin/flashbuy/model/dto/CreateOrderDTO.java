package com.zljin.flashbuy.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderDTO {

    @NotNull(message = "商品id不能为空")
    private String itemId;

    @Min(1)
    private Integer amount;

    private String promoId;
}
