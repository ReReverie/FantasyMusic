package com.fantasy.fm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MusicVO {
    private Long id;
    private String title; // 歌曲标题
    private String artist; // 歌手
    private String album; // 专辑
    private Long durationMs; // 歌曲时长(毫秒表示)
}
