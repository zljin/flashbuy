CREATE TABLE `order_info` (
                              `id` CHAR(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单主键',
                              `user_id` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
                              `item_id` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品ID',
                              `item_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '商品单价',
                              `amount` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '购买数量',
                              `order_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总价',
                              `promo_id` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '活动ID',
                              `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
                              PRIMARY KEY (`id`) USING BTREE,
                              KEY `idx_user_id` (`user_id`),
                              KEY `idx_item_id` (`item_id`),
                              CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`),
                              CONSTRAINT `fk_order_item` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='订单表';