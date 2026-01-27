package com.fantasy.fm.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用于表示音乐信息的实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("music")
public class Music implements Serializable {
    private Long id;
    private String title; // 歌曲标题
    private String artist; // 歌手
    private String album; // 专辑
    private Long durationMs; // 歌曲时长(毫秒表示)
    private String releaseYear; // 发行年份
}
