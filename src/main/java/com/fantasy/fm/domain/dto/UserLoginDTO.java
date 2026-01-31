package com.fantasy.fm.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录DTO")
public class UserLoginDTO {
    @Schema(description = "用户名", example = "john_doe")
    private String username;
    @Schema(description = "密码", example = "securePassword123")
    private String password;
}
