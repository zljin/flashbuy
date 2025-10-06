package com.zljin.flashbuy.mapper;

import com.zljin.flashbuy.domain.ItemStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
* @author zoulingjin
* @description 针对表【item_stock(商品库存表)】的数据库操作Mapper
* @createDate 2025-10-06 11:18:57
* @Entity generator.domain.ItemStock
*/
public interface ItemStockMapper extends BaseMapper<ItemStock> {

    /**
     * 扣减库存
     * @return 受影响的行数
     */
    @Update("UPDATE item_stock SET stock = stock - #{amount} WHERE item_id = #{itemId} AND stock >= #{amount}")
    int decreaseStock(@Param("itemId") Long itemId, @Param("amount") Integer amount);

    /**
     * 增加销量
     * @return 受影响的行数
     */
    @Update("UPDATE item SET sales = sales + #{amount} WHERE id = #{itemId}")
    int increaseSales(@Param("itemId") Long itemId, @Param("amount") Integer amount);

}




