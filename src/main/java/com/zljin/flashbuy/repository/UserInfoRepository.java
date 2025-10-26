package com.zljin.flashbuy.repository;

import com.zljin.flashbuy.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户信息Repository
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
    
    /**
     * 根据手机号查询用户
     */
    @Query("SELECT u FROM UserInfo u WHERE u.telephone = :telephone AND u.isDeleted = 0")
    UserInfo findByTelephoneAndIsDeleted(@Param("telephone") String telephone);
    
    /**
     * 根据邮箱查询用户
     */
    @Query("SELECT u FROM UserInfo u WHERE u.email = :email AND u.isDeleted = 0")
    UserInfo findByEmailAndIsDeleted(@Param("email") String email);
    
    /**
     * 根据用户名查询用户
     */
    @Query("SELECT u FROM UserInfo u WHERE u.name = :name AND u.isDeleted = 0")
    UserInfo findByNameAndIsDeleted(@Param("name") String name);
}
