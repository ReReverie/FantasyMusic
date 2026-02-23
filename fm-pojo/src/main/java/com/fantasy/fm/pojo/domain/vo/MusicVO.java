package com.fantasy.fm.pojo.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "音乐视图对象")
public class MusicVO {
    private Long id;
    private String title; // 歌曲标题
    private String artist; // 歌手
    private String album; // 专辑
    private Long durationMs; // 歌曲时长(毫秒表示)
    private String coverUrl; // 封面图片URL
}
