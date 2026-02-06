package com.fantasy.fm.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "重置密码DTO")
public class ResetPasswordDTO {
    @Schema(description = "账号，可以是用户名或邮箱")
    private String account;
    @Schema(description = "验证码", example = "123456")
    private String code;
    @Schema(description = "新密码")
    private String newPassword;
}
