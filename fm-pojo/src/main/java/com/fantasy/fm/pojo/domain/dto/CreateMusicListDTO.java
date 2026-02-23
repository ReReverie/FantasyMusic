package com.fantasy.fm.pojo.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建歌单DTO")
public class CreateMusicListDTO {
    @JsonIgnore
    private Long userId;
    @Schema(description = "歌单标题", example = "我的歌单")
    private String title;
    @Schema(description = "歌单描述", example = "这是我的个人歌单，包含我喜欢的歌曲。")
    private String description;
}
