package com.fantasy.fm.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新歌单信息DTO")
public class UpdateMusicListDTO {
    private Long id;
    @JsonIgnore
    private Long userId;
    private String title;
    private String description;
    private String cover;
}
