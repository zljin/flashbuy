package com.zljin.flashbuy.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ItemDTO {
    private String id;

    @NotBlank(message = "商品名称不能为空")
    private String title;

    @NotNull(message = "商品价格不能为空")
    @Min(value = 0,message = "商品价格必须大于0")
    private BigDecimal price;

    @NotNull(message = "库存不能不填")
    private Integer stock;

    @NotBlank(message = "商品描述信息不能为空")
    private String description;

    private Integer sales;//销量

    @NotBlank(message = "商品图片信息不能为空")
    private String imgUrl;

    private Promo promo = new Promo();

    @Data
    @NoArgsConstructor
    public static class Promo {
        private String id;

        //秒杀活动状态：1表示还未开始，2表示正在进行，3表示已结束
        private Integer status = 0;

        private String promoName;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date startDate;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date endDate;

        private String itemId;

        private BigDecimal promoItemPrice;
    }
}
