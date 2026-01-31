package com.fantasy.fm.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "用户登录视图对象")
public class UserLoginVO {
    @Schema(description = "用户ID", example = "1")
    private Long id;
    @Schema(description = "用户名", example = "fantasy_user")
    private String username;
    @Schema(description = "昵称", example = "幻想音乐迷")
    private String nickname;
    @Schema(description = "登录令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}
