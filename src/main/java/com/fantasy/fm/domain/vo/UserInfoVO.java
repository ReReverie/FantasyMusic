package com.fantasy.fm.domain.vo;

import com.fantasy.fm.enums.UserLevelEnum;
import lombok.Data;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatarUrl; // 头像URL
    private UserLevelEnum userLevelValue; // 用户等级
}
