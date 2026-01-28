package com.fantasy.fm.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 歌单详细信息视图对象，继承自MusicListVO，包含更多字段
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承时包含父类字段
public class MusicListDetailVO extends MusicListVO {
    private Long userId;
}
