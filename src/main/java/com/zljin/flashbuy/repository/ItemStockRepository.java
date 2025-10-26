package com.zljin.flashbuy.repository;

import com.zljin.flashbuy.domain.ItemStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品库存Repository
 */
@Repository
public interface ItemStockRepository extends JpaRepository<ItemStock, String> {
    
    /**
     * 根据商品ID查询库存
     */
    @Query("SELECT s FROM ItemStock s WHERE s.itemId = :itemId AND s.isDeleted = 0")
    ItemStock findByItemIdAndIsDeleted(@Param("itemId") String itemId);
    
    /**
     * 减少库存
     */
    @Modifying
    @Transactional
    @Query("UPDATE ItemStock s SET s.stock = s.stock - :amount WHERE s.itemId = :itemId AND s.stock >= :amount AND s.isDeleted = 0")
    int decreaseStock(@Param("itemId") String itemId, @Param("amount") Integer amount);
    
}
