package com.fantasy.fm.domain.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatarUrl; // 头像URL
    private Integer userLevelValue; // 用户等级
}
