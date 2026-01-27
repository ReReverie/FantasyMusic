package com.fantasy.fm.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fantasy.fm.enums.UserLevelEnum;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@Builder
@TableName("user")
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username; // 用户名
    private String nickname; // 昵称
    private String email; // 邮箱
    private String password; // 密码（加密存储）
    private String avatarUrl; // 头像URL
    private Integer userLevelValue; // 用户等级
    private LocalDateTime createdTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
