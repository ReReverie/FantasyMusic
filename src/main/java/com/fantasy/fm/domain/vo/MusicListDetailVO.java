package com.fantasy.fm.domain.vo;

import com.fantasy.fm.domain.entity.Music;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true) // 继承时包含父类字段
public class MusicListDetailVO extends MusicListVO {
    private Long userId;

    // --- 详情页独有的字段 ---
    /**
     * 歌单内的歌曲列表
     */
    private List<Music> musics;
}
