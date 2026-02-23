package com.fantasy.fm.pojo.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新用户密码DTO")
public class UpdatePasswordDTO {
    @Schema(description = "用户的ID", example = "1")
    private Long userId;
    @Schema(description = "旧密码", example = "oldPassword123")
    private String oldPassword;
    @Schema(description = "新密码", example = "newPassword456")
    private String newPassword;
}
