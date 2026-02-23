package com.fantasy.fm.pojo.domain.vo;

import com.fantasy.fm.pojo.domain.entity.Music;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 歌单详细信息视图对象，继承自MusicListVO，包含更多字段
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承时包含父类字段
@Schema(description = "音乐歌单详细信息视图对象")
public class MusicListDetailVO extends MusicListVO {
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /**
     * 歌单内的歌曲列表
     */
    @Schema(description = "歌单内的歌曲列表")
    private List<Music> musics;

}
