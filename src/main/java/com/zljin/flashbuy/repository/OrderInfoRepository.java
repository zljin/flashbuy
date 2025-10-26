package com.zljin.flashbuy.repository;

import com.zljin.flashbuy.domain.OrderInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单Repository
 */
@Repository
public interface OrderInfoRepository extends JpaRepository<OrderInfo, String> {
    
    /**
     * 根据用户ID查询订单
     */
    @Query("SELECT o FROM OrderInfo o WHERE o.userId = :userId AND o.isDeleted = 0 ORDER BY o.createdAt DESC")
    Page<OrderInfo> findByUserIdAndIsDeletedOrderByCreatedAtDesc(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 根据用户ID和商品ID查询订单
     */
    @Query("SELECT o FROM OrderInfo o WHERE o.userId = :userId AND o.itemId = :itemId AND o.isDeleted = 0")
    List<OrderInfo> findByUserIdAndItemIdAndIsDeleted(@Param("userId") String userId, @Param("itemId") String itemId);
}
