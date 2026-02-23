package com.fantasy.fm.pojo.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "操作歌单中的音乐DTO")
public class OperaMusicListDTO {
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    @Schema(description = "歌单ID", example = "10")
    private Long musicListId;
    @Schema(description = "音乐ID", example = "100")
    private Long musicId;
}
