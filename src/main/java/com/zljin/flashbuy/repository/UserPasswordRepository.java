package com.zljin.flashbuy.repository;

import com.zljin.flashbuy.domain.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户密码Repository
 */
@Repository
public interface UserPasswordRepository extends JpaRepository<UserPassword, String> {
    
    /**
     * 根据用户ID查询密码
     */
    @Query("SELECT p FROM UserPassword p WHERE p.userId = :userId")
    UserPassword findByUserId(@Param("userId") String userId);
}
