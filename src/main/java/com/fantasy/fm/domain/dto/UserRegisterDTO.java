package com.fantasy.fm.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户注册DTO")
public class UserRegisterDTO {
    @Schema(description = "用户名", example = "john_doe")
    private String username;
    @Schema(description = "密码", example = "securePassword123")
    private String password;
    @Schema(description = "邮箱", example = "xxxx@xx.com")
    private String email;
    @Schema(description = "邮箱验证码", example = "123456")
    private String emailCode;
}
