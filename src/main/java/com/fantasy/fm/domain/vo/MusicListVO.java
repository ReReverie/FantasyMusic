package com.fantasy.fm.domain.vo;

import com.fantasy.fm.domain.entity.Music;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "歌单详细信息视图对象")
public class MusicListVO {
    @Schema(description = "歌单ID", example = "1")
    private Long id;
    /**
     * 歌单标题
     */
    @Schema(description = "歌单标题", example = "我的最爱")
    private String title;

    /**
     * 歌单简介
     */
    @Schema(description = "歌单简介", example = "收藏了我喜欢的音乐")
    private String description;

    /**
     * 封面图片URL
     */
    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String cover;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-01T12:00:00")
    private LocalDateTime createTime;

    /**
     * 歌单内的歌曲列表
     */
    @Schema(description = "歌单内的歌曲列表")
    private List<Music> musics;
}
