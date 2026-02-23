package com.fantasy.fm.pojo.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录DTO")
public class UserLoginDTO {
    @Schema(description = "用户名或者邮箱", example = "john_doe 或 邮箱")
    private String account;
    @Schema(description = "密码", example = "securePassword123")
    private String password;
    @Schema(description = "验证码", example = "a1b2")
    private String captchaCode;
    @Schema(description = "验证码UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String captchaUuid;
}
