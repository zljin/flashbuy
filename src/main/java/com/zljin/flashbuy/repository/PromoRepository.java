package com.zljin.flashbuy.repository;

import com.zljin.flashbuy.domain.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 活动Repository
 */
@Repository
public interface PromoRepository extends JpaRepository<Promo, String> {
    
    /**
     * 根据商品ID查询活动
     */
    @Query("SELECT p FROM Promo p WHERE p.itemId = :itemId AND p.isDeleted = 0")
    Promo findByItemIdAndIsDeleted(@Param("itemId") String itemId);
}
