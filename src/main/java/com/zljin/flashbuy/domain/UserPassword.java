package com.zljin.flashbuy.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

/**
 * 用户密码表
 */
@Entity
@Table(name = "user_password")
@Data
public class UserPassword {
    /**
     * 主键，自增ID
     */
    @Id
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.zljin.flashbuy.util.SnowflakeIdGenerator")
    @Column(name = "id")
    private String id;

    /**
     * 加密密码
     */
    @Column(name = "encrpt_password")
    private String encrptPassword;

    /**
     * 用户ID，关联 user_info 表
     */
    @Column(name = "user_id")
    private String userId;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        UserPassword other = (UserPassword) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getEncrptPassword() == null ? other.getEncrptPassword() == null : this.getEncrptPassword().equals(other.getEncrptPassword()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getEncrptPassword() == null) ? 0 : getEncrptPassword().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", encrptPassword=").append(encrptPassword);
        sb.append(", userId=").append(userId);
        sb.append("]");
        return sb.toString();
    }
}