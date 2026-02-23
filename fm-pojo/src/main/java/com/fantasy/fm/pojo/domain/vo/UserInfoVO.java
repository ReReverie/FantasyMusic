package com.fantasy.fm.pojo.domain.vo;

import com.fantasy.fm.common.enums.UserLevelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息视图对象")
public class UserInfoVO {
    @Schema(description = "用户ID", example = "1")
    private Long id;
    @Schema(description = "用户名", example = "fantasy_user")
    private String username;
    @Schema(description = "昵称", example = "幻想音乐迷")
    private String nickname;
    @Schema(description = "电子邮箱", example = "example@example.com")
    private String email;
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl; // 头像URL
    @Schema(description = "用户等级", example = "VIP")
    private UserLevelEnum userLevelValue; // 用户等级
}
