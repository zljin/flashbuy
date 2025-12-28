package com.zljin.flashbuy.repository;

import com.zljin.flashbuy.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 商品Repository
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    
    /**
     * 根据标题模糊查询商品
     */
    @Query("SELECT i FROM Item i WHERE i.title LIKE %:title% AND i.isDeleted = 0 ORDER BY i.createdAt DESC")
    Page<Item> findByTitleContainingAndIsDeletedOrderByCreatedAtDesc(@Param("title") String title, Pageable pageable);
    
    /**
     * 查询所有未删除的商品
     */
    @Query("SELECT i FROM Item i WHERE i.isDeleted = 0 ORDER BY i.createdAt DESC")
    Page<Item> findByIsDeletedOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * 根据ID查询未删除的商品
     */
    @Query("SELECT i FROM Item i WHERE i.id = :id AND i.isDeleted = 0")
    Item findByIdAndIsDeleted(@Param("id") String id);
    
    /**
     * 增加销量
     */
    @Modifying
    @Query("UPDATE Item i SET i.sales = i.sales + :amount WHERE i.id = :itemId AND i.isDeleted = 0")
    int increaseSales(@Param("itemId") String itemId, @Param("amount") Integer amount);
}
