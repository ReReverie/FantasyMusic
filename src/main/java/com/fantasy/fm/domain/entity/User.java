package com.fantasy.fm.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fantasy.fm.enums.UserLevelEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("user")
public class User {
    private Long id;
    private String username; // 用户名
    private String nickname; // 昵称
    private String email; // 邮箱
    private String password; // 密码（加密存储）
    private String avatarUrl; // 头像URL
    private Integer userLevelValue; // 用户等级
    private UserLevelEnum userLevel; // 用户等级枚举
    private UserMusicInfo userMusicInfo; // 用户的音乐信息
    private LocalDateTime createdTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间（时间戳）
}
