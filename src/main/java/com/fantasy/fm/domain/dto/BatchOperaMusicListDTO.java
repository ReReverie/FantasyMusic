package com.fantasy.fm.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量操作歌单中的音乐DTO")
public class BatchOperaMusicListDTO {
    @JsonIgnore
    private Long userId;
    @Schema(description = "歌单ID", example = "10")
    private Long musicListId;
    @Schema(description = "音乐ID", example = "100")
    private List<Long> musicIds;
}
